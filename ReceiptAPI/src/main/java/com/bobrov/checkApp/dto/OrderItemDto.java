package com.bobrov.checkApp.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link com.bobrov.checkApp.model.OrderItem} entity
 */
@Data
public class OrderItemDto implements Serializable {
    private final Integer quantity;
    private final ProductDto product;
}