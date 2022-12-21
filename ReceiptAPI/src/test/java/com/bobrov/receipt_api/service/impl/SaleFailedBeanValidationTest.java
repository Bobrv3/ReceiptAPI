package com.bobrov.receipt_api.service.impl;

import com.bobrov.receipt_api.dto.SaleDto;
import com.bobrov.receipt_api.service.SaleService;
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
public class SaleFailedBeanValidationTest {
    @Autowired
    private SaleService service;
    private SaleDto saleDtoBadAmountOfDigitsAndQuantity = SaleDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(1.1245))
            .fromQuantity(-2)
            .build();
    private SaleDto saleDtoDiscountIsNegative = SaleDto.builder()
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
    @DisplayName("save: not valid saleDto bad Amount Of Digits")
    void should_throw_if_badAmountOfDigits() {
        assertThatThrownBy(() -> service.save(saleDtoBadAmountOfDigitsAndQuantity))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("discountSize: число вне допустимого диапазона (ожидалось <4 разрядов>.<2 разрядов>)")
                .hasMessageContaining("fromQuantity: должно быть не меньше 1");
    }

    @Test
    @DisplayName("save: not valid saleDto discount size is negative")
    void should_throw_if_discountSizeIsNegative() {
        assertThatThrownBy(() -> service.save(saleDtoDiscountIsNegative))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("discountSize: должно быть больше, чем 0.0");
    }

    @Test
    @DisplayName("update: not valid saleDto bad Amount Of Digits")
    void update_should_throw_if_badAmountOfDigits() {
        assertThatThrownBy(() -> service.update(0L, saleDtoBadAmountOfDigitsAndQuantity))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("id: должно быть не меньше 1")
                .hasMessageContaining("discountSize: число вне допустимого диапазона (ожидалось <4 разрядов>.<2 разрядов>)")
                .hasMessageContaining("fromQuantity: должно быть не меньше 1");
    }

    @Test
    @DisplayName("update: not valid saleDto discount size is negative")
    void update_should_throw_if_discountSizeIsNegative() {
        assertThatThrownBy(() -> service.update(1L, saleDtoDiscountIsNegative))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("discountSize: должно быть больше, чем 0.0");
    }

    @Test
    @DisplayName("deleteById: not valid id")
    void deleteById() {
        assertThatThrownBy(() -> service.deleteById(0L))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("id: должно быть не меньше 1");
    }
}
