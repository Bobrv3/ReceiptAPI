package com.bobrov.receipt_api.dao;

import com.bobrov.receipt_api.model.DiscountCard;
import com.bobrov.receipt_api.model.Order;
import com.bobrov.receipt_api.model.OrderItem;
import com.bobrov.receipt_api.model.Product;
import com.bobrov.receipt_api.model.Sale;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@SqlGroup({
        @Sql("/schema.sql"),
        @Sql("/test_data.sql")

})
class OrderRepositoryTest {
    private static final int INIT_SIZE = 2;
    private Sale sale = Sale.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();
    private DiscountCard card = DiscountCard.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(5))
            .build();
    private Product product = Product.builder()
            .id(1L)
            .description("Milk")
            .price(BigDecimal.valueOf(1.6))
            .sale(sale)
            .build();

    @Autowired
    OrderRepository repository;

    @Test
    @DisplayName("Test order mapping")
    public void testMapping() {
        Order order = Order.builder()
                .id(1L)
                .discountCard(card)
                .build();
        order.addItem(OrderItem.builder()
                .product(product)
                .quantity(6)
                .build());

        repository.findById(order.getId());

        Order reference = repository.getReferenceById(order.getId());

        List<Order> orders = repository.findAll();

        assertAll(
                () -> assertEquals(reference.getDiscountCard(), order.getDiscountCard()),
                () -> assertThat(orders)
                        .hasSize(INIT_SIZE)
        );
    }
}