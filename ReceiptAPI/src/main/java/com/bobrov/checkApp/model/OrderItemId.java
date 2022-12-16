package com.bobrov.checkApp.model;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
public class OrderItemId implements Serializable {
    private static final long serialVersionUID = -6913215028377128575L;
    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false)
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemId orderItemId = (OrderItemId) o;
        return Objects.equals(orderId, orderItemId.orderId) && Objects.equals(productId, orderItemId.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, productId);
    }
}
