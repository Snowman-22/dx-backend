package com.snowman.team2.domain.product.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubscribePriceId implements Serializable {

    private Long subscribeId;
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscribePriceId that = (SubscribePriceId) o;
        return Objects.equals(subscribeId, that.subscribeId) && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscribeId, productId);
    }
}
