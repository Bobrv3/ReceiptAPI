package com.bobrov.receipt_api.dao;

import com.bobrov.receipt_api.model.DiscountCard;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup({
        @Sql("/schema.sql"),
        @Sql("/test_data.sql")

})
class DiscountCardRepositoryTest {
    private static final int INIT_SIZE = 1;
    private DiscountCard card = DiscountCard.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(5))
            .build();

    @Autowired
    DiscountCardRepository repository;

    @Test
    public void should_findCards_if_repositoryIsNotEmpty() {
        List<DiscountCard> cards = repository.findAll();

        assertThat(cards)
                .hasSize(INIT_SIZE)
                .contains(card);
    }

}