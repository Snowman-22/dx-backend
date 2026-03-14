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

    private Long d2_details_id;
    private Long layout_2d_id;
    private Long product_id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        D2DetailsId that = (D2DetailsId) o;
        return Objects.equals(d2_details_id, that.d2_details_id)
                && Objects.equals(layout_2d_id, that.layout_2d_id)
                && Objects.equals(product_id, that.product_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(d2_details_id, layout_2d_id, product_id);
    }
}
