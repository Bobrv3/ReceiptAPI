package com.bobrov.receipt_api.dto;

import com.bobrov.receipt_api.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link Product} entity
 */
@Data
@Builder
@AllArgsConstructor
public class ProductDto implements Serializable {
    @Min(value = 1)
    private Long id;

    @NotBlank
    @Size(min = 2, max = 16)
    private String description;

    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer=4, fraction=2)
    private BigDecimal price;

    @Valid
    private SaleDto sale;

    private Product.ProductStatus status;
}