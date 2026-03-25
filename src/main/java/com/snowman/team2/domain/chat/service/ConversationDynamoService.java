package com.snowman.team2.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.snowman.team2.domain.chat.dto.ChatMessage;
import com.snowman.team2.domain.chat.dto.FastApiChatResponse;
import com.snowman.team2.domain.chat.entity.ChatEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConversationDynamoService {

    private final DynamoDbClient dynamoDbClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.dynamodb.table.conversation:conversation}")
    private String conversationTableName;

    @Value("${aws.dynamodb.table.product-spec-details:product_spec_details}")
    private String productSpecDetailsTableName;

    public void initializeConversation(ChatEntity chat) {
        String convId = extractConvId(chat);
        Map<String, AttributeValue> item = new LinkedHashMap<>();
        item.put("chat_id", stringValue(convId));
        item.put("user_id", nullableNumberValue(chat.getUser() == null ? null : chat.getUser().getUserId()));
        item.put("starterpackage_id", numberValue(chat.getStarterPackage().getStarterPackageId()));
        item.put("chat_title", nullableStringValue(chat.getChatTitle()));
        item.put("start_date", stringValue(toIso(chat.getStartDate())));
        item.put("messages", AttributeValue.builder().l(List.of()).build());

        dynamoDbClient.putItem(PutItemRequest.builder()
                .tableName(conversationTableName)
                .item(item)
                .build());
    }

    public void appendUserMessage(ChatEntity chat, ChatMessage message) {
        String convId = extractConvId(chat);
        Map<String, AttributeValue> messagePayload = new LinkedHashMap<>();
        messagePayload.put("role", stringValue("USER"));
        messagePayload.put("step_code", nullableStringValue(message.stepCode()));
        messagePayload.put("assistant_text", nullableStringValue(message.assistantText()));
        messagePayload.put("payload_json", stringValue(toJson(message.userText())));
        messagePayload.put("created_at", stringValue(nowIso()));

        appendMessage(convId, messagePayload);
    }

    public void appendAssistantMessage(ChatEntity chat, FastApiChatResponse response) {
        String convId = extractConvId(chat);
        Map<String, AttributeValue> messagePayload = new LinkedHashMap<>();
        messagePayload.put("role", stringValue("ASSISTANT"));
        messagePayload.put("ai_response", nullableStringValue(response.aiResponse()));
        messagePayload.put("data_json", stringValue(toJson(response.data())));
        messagePayload.put("created_at", stringValue(nowIso()));

        appendMessage(convId, messagePayload);
    }

    public Map<String, Object> getConversationByConvId(String convId) {
        Map<String, AttributeValue> item = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(conversationTableName)
                .key(Map.of("chat_id", stringValue(convId)))
                .build())
                .item();
        return toPlainMap(item);
    }

    public Map<String, Object> getProductSpecDetailsByModelId(String modelId) {
        Map<String, AttributeValue> item = dynamoDbClient.getItem(GetItemRequest.builder()
                .tableName(productSpecDetailsTableName)
                .key(Map.of("model_id", stringValue(modelId)))
                .build())
                .item();
        return toPlainMap(item);
    }

    public void updateChatTitle(String convId, String chatTitle) {
        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(conversationTableName)
                .key(Map.of("chat_id", stringValue(convId)))
                .updateExpression("SET chat_title = :chatTitle")
                .expressionAttributeValues(Map.of(":chatTitle", stringValue(chatTitle)))
                .build());
    }

    private void appendMessage(String convId, Map<String, AttributeValue> messagePayload) {
        Map<String, String> expressionNames = Map.of("#messages", "messages");
        Map<String, AttributeValue> expressionValues = Map.of(
                ":empty", AttributeValue.builder().l(List.of()).build(),
                ":message", AttributeValue.builder()
                        .l(AttributeValue.builder().m(messagePayload).build())
                        .build()
        );

        dynamoDbClient.updateItem(UpdateItemRequest.builder()
                .tableName(conversationTableName)
                .key(Map.of("chat_id", stringValue(convId)))
                .updateExpression("SET #messages = list_append(if_not_exists(#messages, :empty), :message)")
                .expressionAttributeNames(expressionNames)
                .expressionAttributeValues(expressionValues)
                .build());
    }

    private String extractConvId(ChatEntity chat) {
        if (chat.getChatConvId() == null || chat.getChatConvId().isBlank()) {
            throw new IllegalArgumentException("chatConvId is required for DynamoDB conversation logging.");
        }
        return chat.getChatConvId();
    }

    private AttributeValue stringValue(String value) {
        return AttributeValue.builder().s(value).build();
    }

    private AttributeValue nullableStringValue(String value) {
        if (value == null) {
            return AttributeValue.builder().nul(true).build();
        }
        return stringValue(value);
    }

    private AttributeValue numberValue(Number value) {
        return AttributeValue.builder().n(String.valueOf(value)).build();
    }

    private AttributeValue nullableNumberValue(Number value) {
        if (value == null) {
            return AttributeValue.builder().nul(true).build();
        }
        return numberValue(value);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize conversation payload.", e);
        }
    }

    private String nowIso() {
        return LocalDateTime.now().toString();
    }

    private String toIso(LocalDateTime value) {
        return value.atOffset(ZoneOffset.UTC).toString();
    }

    private Map<String, Object> toPlainMap(Map<String, AttributeValue> item) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (item == null || item.isEmpty()) {
            return result;
        }
        for (Map.Entry<String, AttributeValue> entry : item.entrySet()) {
            result.put(entry.getKey(), toPlainValue(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    private Object toPlainValue(String key, AttributeValue value) {
        if (value == null) {
            return null;
        }
        if (Boolean.TRUE.equals(value.nul())) {
            return null;
        }
        if (value.s() != null) {
            if (key != null && key.endsWith("_json")) {
                return parseJsonSafely(value.s());
            }
            return value.s();
        }
        if (value.n() != null) {
            return value.n().contains(".") ? Double.parseDouble(value.n()) : Long.parseLong(value.n());
        }
        if (value.bool() != null) {
            return value.bool();
        }
        if (value.hasM()) {
            Map<String, Object> nested = new LinkedHashMap<>();
            for (Map.Entry<String, AttributeValue> entry : value.m().entrySet()) {
                nested.put(entry.getKey(), toPlainValue(entry.getKey(), entry.getValue()));
            }
            return nested;
        }
        if (value.hasL()) {
            List<Object> nested = new ArrayList<>(value.l().size());
            for (AttributeValue item : value.l()) {
                nested.add(toPlainValue(key, item));
            }
            return nested;
        }
        return null;
    }

    private Object parseJsonSafely(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception ignored) {
            return json;
        }
    }
}
