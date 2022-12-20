package com.bobrov.receipt_api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {
    @EmbeddedId
    @Builder.Default
    private OrderItemId id = new OrderItemId();

    @Column(nullable = false)
    private Integer quantity;

    @MapsId("productId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Product product;

    @MapsId("orderId")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Order order;

    void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        OrderItem orderItem = (OrderItem) o;
        return id != null && Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
