package com.bobrov.checkApp.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Where(clause = "status = 0")
public class Order {
    public enum OrderStatus {
        ENABLE, DELETED
    }

    private static final String SEQ_NAME = "order_seq";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_NAME)
    @SequenceGenerator(name = SEQ_NAME, sequenceName = SEQ_NAME, allocationSize = 1)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private DiscountCard discountCard;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            orphanRemoval = true,
            fetch = FetchType.LAZY,
            mappedBy = "order")
    @Builder.Default
    @Setter(AccessLevel.NONE)
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.ENABLE;

    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }
}
