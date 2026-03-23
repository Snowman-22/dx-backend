package com.snowman.team2.domain.cart.repository;

import com.snowman.team2.domain.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    boolean existsByUser_UserIdAndProduct_ProductIdAndIsDeleteFalse(Long userId, Long productId);
    java.util.Optional<CartEntity> findByCartIdAndUser_UserIdAndIsDeleteFalse(Long cartId, Long userId);
    java.util.Optional<CartEntity> findByUser_UserIdAndChatConvIdAndProduct_ProductIdAndIsDeleteFalse(
            Long userId,
            String chatConvId,
            Long productId
    );
    java.util.List<CartEntity> findAllByUser_UserIdAndChatConvIdAndIsDeleteFalseOrderByCreateDateDesc(
            Long userId,
            String chatConvId
    );
    java.util.List<CartEntity> findAllByUser_UserIdAndIsDeleteFalseAndProduct_ProductIdInOrderByCreateDateDesc(
            Long userId,
            java.util.Collection<Long> productIds
    );
    java.util.List<CartEntity> findAllByUser_UserIdAndChatConvIdAndIsDeleteFalseAndProduct_ProductIdInOrderByCreateDateDesc(
            Long userId,
            String chatConvId,
            java.util.Collection<Long> productIds
    );
}
