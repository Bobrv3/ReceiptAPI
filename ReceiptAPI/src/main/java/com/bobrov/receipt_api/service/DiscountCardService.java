package com.bobrov.receipt_api.service;

import com.bobrov.receipt_api.dto.DiscountCardDto;
import com.bobrov.receipt_api.model.DiscountCard;
import org.springframework.data.domain.Page;

import javax.validation.Valid;
import javax.validation.constraints.Min;

public interface DiscountCardService {
    DiscountCard findById(@Min(1)Long id);

    Page<DiscountCard> findAll(@Min(0) Integer offset, @Min(1) Integer limit);

    DiscountCard save(@Valid DiscountCardDto cardDto);

    DiscountCard update(@Min(1) Long id, @Valid DiscountCardDto cardDto);

    void deleteById(@Min(1) Long id);
}
