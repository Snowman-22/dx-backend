package com.snowman.team2.domain.product.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubscribePriceId implements Serializable {

    private Long subscribe_id;
    private Long product_id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubscribePriceId that = (SubscribePriceId) o;
        return Objects.equals(subscribe_id, that.subscribe_id) && Objects.equals(product_id, that.product_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subscribe_id, product_id);
    }
}
