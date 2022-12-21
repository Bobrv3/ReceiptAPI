package com.bobrov.receipt_api.service.impl;

import com.bobrov.receipt_api.dto.DiscountCardDto;
import com.bobrov.receipt_api.service.DiscountCardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DiscountCardFailedBeanValidationTest {
    @Autowired
    private DiscountCardService service;
    private DiscountCardDto cardDtoBadAmountOfDigits = DiscountCardDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(1.1245))
            .build();
    private DiscountCardDto cardDtoDiscountIsNegative = DiscountCardDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(-1.1245))
            .build();

    @Test
    @DisplayName("findById: not valid id")
    void findById() {
        assertThatThrownBy(() -> service.findById(0L))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("id: должно быть не меньше 1");
    }

    @Test
    @DisplayName("save: not valid cardDto bad Amount Of Digits")
    void should_throw_if_badAmountOfDigits() {
        assertThatThrownBy(() -> service.save(cardDtoBadAmountOfDigits))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("discountSize: число вне допустимого диапазона (ожидалось <4 разрядов>.<2 разрядов>)");
    }

    @Test
    @DisplayName("save: not valid cardDto discount size is negative")
    void should_throw_if_discountSizeIsNegative() {
        assertThatThrownBy(() -> service.save(cardDtoDiscountIsNegative))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("discountSize: должно быть больше, чем или равно 0.0");
    }

    @Test
    @DisplayName("update: not valid cardDto bad Amount Of Digits")
    void update_should_throw_if_badAmountOfDigits() {
        assertThatThrownBy(() -> service.update(0L, cardDtoBadAmountOfDigits))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("id: должно быть не меньше 1")
                .hasMessageContaining("discountSize: число вне допустимого диапазона (ожидалось <4 разрядов>.<2 разрядов>)");
    }

    @Test
    @DisplayName("update: not valid cardDto discount size is negative")
    void update_should_throw_if_discountSizeIsNegative() {
        assertThatThrownBy(() -> service.update(1L, cardDtoDiscountIsNegative))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("discountSize: должно быть больше, чем или равно 0.0");
    }

    @Test
    @DisplayName("deleteById: not valid id")
    void deleteById() {
        assertThatThrownBy(() -> service.deleteById(0L))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("id: должно быть не меньше 1");
    }
}
