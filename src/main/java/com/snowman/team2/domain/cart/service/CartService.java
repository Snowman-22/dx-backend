package com.snowman.team2.domain.cart.service;

import com.snowman.team2.domain.cart.entity.CartEntity;
import com.snowman.team2.domain.cart.repository.CartRepository;
import com.snowman.team2.global.exception.ErrorCode;
import com.snowman.team2.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;

    @Transactional
    public void deleteCartItem(Long cartId, Long userId) {
        CartEntity cart = cartRepository.findByCartIdAndUser_UserIdAndIsDeleteFalse(cartId, userId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.DATA_NOT_EXIST, "삭제할 카트 항목을 찾을 수 없습니다."));
        cart.markDeleted();
    }
}
