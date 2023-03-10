package com.bobrov.receipt_api.dao;

import com.bobrov.receipt_api.model.DiscountCard;
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
class DiscountCardRepositoryTest {
    private static final int INIT_SIZE = 1;
    private DiscountCard card = DiscountCard.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(5))
            .build();

    @Autowired
    DiscountCardRepository repository;

    @Test
    @DisplayName("Test discountCard mapping")
    public void testMapping() {
        repository.findById(card.getId());

        DiscountCard reference = repository.getReferenceById(card.getId());

        List<DiscountCard> cards = repository.findAll();

        assertAll(
                () -> assertEquals(reference.getDiscountSize(), card.getDiscountSize()),
                () -> assertThat(cards)
                        .hasSize(INIT_SIZE)
                        .contains(card)
        );
    }
}