package com.snowman.team2.domain.cart.repository;

import com.snowman.team2.domain.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    boolean existsByUser_UserIdAndProduct_ProductIdAndIsDeleteFalse(Long userId, Long productId);
    java.util.Optional<CartEntity> findByUser_UserIdAndChatConvIdAndProduct_ProductIdAndIsDeleteFalse(
            Long userId,
            String chatConvId,
            Long productId
    );
    java.util.Optional<CartEntity> findByUser_UserIdAndChatConvIdAndRecommendationIdAndProduct_ProductIdAndIsDeleteFalse(
            Long userId,
            String chatConvId,
            Long recommendationId,
            Long productId
    );
    java.util.List<CartEntity> findAllByUser_UserIdAndChatConvIdAndIsDeleteFalseOrderByCreateDateDesc(
            Long userId,
            String chatConvId
    );
    java.util.List<CartEntity> findAllByUser_UserIdAndChatConvIdAndRecommendationIdAndIsDeleteFalseOrderByCreateDateDesc(
            Long userId,
            String chatConvId,
            Long recommendationId
    );
}
