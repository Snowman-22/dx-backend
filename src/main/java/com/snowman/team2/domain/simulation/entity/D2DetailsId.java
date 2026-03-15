package com.snowman.team2.domain.simulation.entity;

import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class D2DetailsId implements Serializable {

    private Long d2DetailsId;
    private Long layout2dId;
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        D2DetailsId that = (D2DetailsId) o;
        return Objects.equals(d2DetailsId, that.d2DetailsId)
                && Objects.equals(layout2dId, that.layout2dId)
                && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(d2DetailsId, layout2dId, productId);
    }
}
