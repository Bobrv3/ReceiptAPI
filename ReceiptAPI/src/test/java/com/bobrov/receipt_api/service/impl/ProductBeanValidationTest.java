package com.bobrov.receipt_api.service.impl;

import com.bobrov.receipt_api.dto.ProductDto;
import com.bobrov.receipt_api.dto.SaleDto;
import com.bobrov.receipt_api.service.ProductService;
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
public class ProductBeanValidationTest {
    @Autowired
    private ProductService service;
    private SaleDto saleDto = SaleDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();
    private ProductDto productDtoBadAmountOfDigitsAndDescr = ProductDto.builder()
            .id(1L)
            .description("MilkMilkMilkMilkMilk")
            .price(BigDecimal.valueOf(1.1245))
            .sale(saleDto)
            .build();
    private ProductDto productDtoPriceIsNegative = ProductDto.builder()
            .id(1L)
            .description("Milk")
            .price(BigDecimal.valueOf(-1.1245))
            .sale(saleDto)
            .build();


    @Test
    @DisplayName("findById: not valid id")
    void findById() {
        assertThatThrownBy(() -> service.findById(0L))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("id: должно быть не меньше 1");
    }

    @Test
    @DisplayName("save: not valid productDto bad Amount Of Digits")
    void should_throw_if_badAmountOfDigits() {
        assertThatThrownBy(() -> service.save(productDtoBadAmountOfDigitsAndDescr))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("price: число вне допустимого диапазона (ожидалось <4 разрядов>.<2 разрядов>)")
                .hasMessageContaining("description: размер должен находиться в диапазоне от 2 до 16");
    }

    @Test
    @DisplayName("save: not valid productDto price is negative")
    void should_throw_if_priceIsNegative() {
        assertThatThrownBy(() -> service.save(productDtoPriceIsNegative))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("price: должно быть больше, чем 0.0");
    }

    @Test
    @DisplayName("update: not valid productDto bad Amount Of Digits")
    void update_should_throw_if_badAmountOfDigits() {
        assertThatThrownBy(() -> service.update(0L, productDtoBadAmountOfDigitsAndDescr))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("id: должно быть не меньше 1")
                .hasMessageContaining("price: число вне допустимого диапазона (ожидалось <4 разрядов>.<2 разрядов>)")
                .hasMessageContaining("description: размер должен находиться в диапазоне от 2 до 16");
    }

    @Test
    @DisplayName("update: not valid productDto price is negative")
    void update_should_throw_if_priceIsNegative() {
        assertThatThrownBy(() -> service.update(1L, productDtoPriceIsNegative))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("price: должно быть больше, чем 0.0");
    }

    @Test
    @DisplayName("deleteById: not valid id")
    void deleteById() {
        assertThatThrownBy(() -> service.deleteById(0L))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("id: должно быть не меньше 1");
    }
}
