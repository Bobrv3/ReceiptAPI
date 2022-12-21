package com.bobrov.receipt_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * A DTO for the {@link com.bobrov.receipt_api.model.OrderItem} entity
 */
@Data
@Builder
@AllArgsConstructor
public class OrderItemDto implements Serializable {
    @Min(1)
    private Integer quantity;
    @Valid
    private ProductDto product;
}