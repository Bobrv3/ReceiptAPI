package com.bobrov.checkApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link com.bobrov.checkApp.model.OrderItem} entity
 */
@Data
@Builder
@AllArgsConstructor
public class OrderItemDto implements Serializable {
    private Integer quantity;
    private ProductDto product;
}