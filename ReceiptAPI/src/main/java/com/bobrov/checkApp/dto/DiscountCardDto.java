package com.bobrov.checkApp.dto;

import com.bobrov.checkApp.model.DiscountCard;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link com.bobrov.checkApp.model.DiscountCard} entity
 */
@Data
public class DiscountCardDto implements Serializable {
    @Min(value = 1)
    private final Long id;

    @DecimalMin(value = "0.0")
    @Digits(integer=4, fraction=2)
    private final BigDecimal discountSize;

    private final DiscountCard.DiscountCardStatus status;
}