package com.bobrov.receipt_api.dao;

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
class SaleRepositoryTest {
    private static final int INIT_SIZE = 1;
    private Sale sale = Sale.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();

    @Autowired
    SaleRepository repository;

    @Test
    @DisplayName("Test sale mapping")
    public void testMapping() {
        repository.findById(sale.getId());

        Sale reference = repository.getReferenceById(sale.getId());

        List<Sale> sales = repository.findAll();

        assertAll(
                () -> assertEquals(reference.getDiscountSize(), sale.getDiscountSize()),
                () -> assertThat(sales)
                        .hasSize(INIT_SIZE)
                        .contains(sale)
        );
    }
}