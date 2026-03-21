package com.snowman.team2.global.storage;

import com.snowman.team2.global.exception.CustomException;
import com.snowman.team2.global.exception.ErrorCode;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService {

    private final S3Template s3Template;
    private final S3Properties s3Properties;

    /**
     * 바이너리를 S3에 올리고, DB에 넣을 <b>객체 키</b>(버킷 내 경로)만 반환합니다.
     * <p>
     * 프론트에 이미지로 보여줄 때는 {@link #presignedGetUrl(String)} 으로 잠깐 쓸 수 있는 URL을 만들어 내려줍니다.
     * <p>
     * {@link S3UploadPath#PRODUCT_IMAGE} → {@code ProductEntity#productImageUrl} (루트 키, 프리픽스 없음)<br>
     * {@link S3UploadPath#BLUEPRINT_IMAGE} → {@code BlueprintEntity#blueprintImageUrl}<br>
     * {@link S3UploadPath#LAYOUT_2D} → {@code D2SimulationEntity#layout2dImgUrl} (프론트 전달 파일 업로드)<br>
     * {@link S3UploadPath#LAYOUT_3D} → {@code D3SimulationEntity#layout3dImgUrl} (프론트 전달 파일 업로드)
     */
    public String upload(S3UploadPath path, MultipartFile file) {
        String key = buildObjectKey(path.getPrefix(), file.getOriginalFilename());
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        ObjectMetadata metadata = ObjectMetadata.builder()
                .contentType(contentType)
                .contentLength(file.getSize())
                .build();
        try (InputStream inputStream = file.getInputStream()) {
            s3Template.upload(s3Properties.getBucket(), key, inputStream, metadata);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_FAIL, e.getMessage());
        }
        return key;
    }

    /**
     * DB에 저장된 객체 키로, 브라우저에서 잠시 쓸 presigned GET URL을 만듭니다.
     * (영구적인 버킷 HTTP 주소를 DB/응답에 고정하지 않기 위한 용도)
     */
    public String presignedGetUrl(String objectKey) {
        if (objectKey == null || objectKey.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_PARAMETER, "S3 객체 키가 비어 있습니다.");
        }
        Duration ttl = Duration.ofMinutes(s3Properties.getPresignedGetTtlMinutes());
        try {
            URL url = s3Template.createSignedGetURL(s3Properties.getBucket(), objectKey, ttl);
            return url.toExternalForm();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "S3 presigned URL 생성에 실패했습니다.");
        }
    }

    private static String buildObjectKey(String prefix, String originalFilename) {
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf('.'));
        }
        String name = UUID.randomUUID() + ext;
        if (prefix == null || prefix.isBlank()) {
            return name;
        }
        return prefix + "/" + name;
    }
}
