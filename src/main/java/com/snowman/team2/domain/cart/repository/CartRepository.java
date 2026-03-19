package com.snowman.team2.domain.cart.repository;

import com.snowman.team2.domain.cart.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    boolean existsByUser_UserIdAndProduct_ProductIdAndIsDeleteFalse(Long userId, Long productId);
    java.util.Optional<CartEntity> findByCartIdAndUser_UserIdAndIsDeleteFalse(Long cartId, Long userId);
}
