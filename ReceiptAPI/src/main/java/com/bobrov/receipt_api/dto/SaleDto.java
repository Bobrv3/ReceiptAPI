package com.bobrov.receipt_api.dto;

import com.bobrov.receipt_api.model.Sale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link com.bobrov.receipt_api.model.Sale} entity
 */
@Data
@Builder
@AllArgsConstructor
public class SaleDto implements Serializable {
    @Min(1)
    private Long id;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=4, fraction=2)
    private BigDecimal discountSize;

    @Min(1)
    private Integer fromQuantity;

    private Sale.SaleStatus status;
}