package com.bobrov.receipt_api.service.impl;

import com.bobrov.receipt_api.dto.DiscountCardDto;
import com.bobrov.receipt_api.dto.OrderDto;
import com.bobrov.receipt_api.dto.OrderItemDto;
import com.bobrov.receipt_api.dto.ProductDto;
import com.bobrov.receipt_api.dto.SaleDto;
import com.bobrov.receipt_api.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderFailedBeanValidationTest {
    @Autowired
    private OrderService service;
    private SaleDto saleDto = SaleDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();
    private DiscountCardDto cardDto = DiscountCardDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(5))
            .build();
    private ProductDto productDtoPriceIsNegative = ProductDto.builder()
            .id(1L)
            .description("Milk")
            .price(BigDecimal.valueOf(-1.1245))
            .sale(saleDto)
            .build();
    private OrderItemDto itemDto = OrderItemDto.builder()
            .product(productDtoPriceIsNegative)
            .quantity(6)
            .build();
    private OrderDto orderDto = OrderDto.builder()
            .id(1L)
            .discountCard(cardDto)
            .items(new ArrayList<>())
            .build();

    public OrderFailedBeanValidationTest() {
        orderDto.getItems().add(itemDto);
    }

    @Test
    @DisplayName("findById: not valid id")
    void findById() {
        assertThatThrownBy(() -> service.findById(0L))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("id: должно быть не меньше 1");
    }

    @Test
    @DisplayName("save: not valid order price is negative")
    void should_throw_if_priceIsNegative() {
        assertThatThrownBy(() -> service.save(orderDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("price: должно быть больше, чем 0.0");
    }

    @Test
    @DisplayName("update: not valid order discount size is negative")
    void update_should_throw_if_priceIsNegative() {
        assertThatThrownBy(() -> service.update(1L, orderDto))
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
