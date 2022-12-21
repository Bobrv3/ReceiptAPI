package com.bobrov.receipt_api.dao;

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
class ProductRepositoryTest {
    private static final int INIT_SIZE = 2;
    private Sale sale = Sale.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();
    private Product product = Product.builder()
            .id(1L)
            .description("Milk")
            .price(BigDecimal.valueOf(1.6))
            .sale(sale)
            .build();

    @Autowired
    ProductRepository repository;

    @Test
    @DisplayName("Test product mapping")
    public void testMapping() {
        repository.findById(product.getId());

        Product reference = repository.getReferenceById(product.getId());

        List<Product> products = repository.findAll();

        assertAll(
                () -> assertEquals(reference.getSale(), product.getSale()),
                () -> assertThat(products)
                        .hasSize(INIT_SIZE)
                        .contains(product)
        );
    }
}