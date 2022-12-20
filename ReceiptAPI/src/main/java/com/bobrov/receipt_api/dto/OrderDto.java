package com.bobrov.receipt_api.dto;

import com.bobrov.receipt_api.model.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link Order} entity
 */
@Data
@Builder
@AllArgsConstructor
public class OrderDto implements Serializable {
    @Min(1)
    private Long id;

    private DiscountCardDto discountCard;

    @NotNull
    private List<OrderItemDto> items;


    private Order.OrderStatus status;
}