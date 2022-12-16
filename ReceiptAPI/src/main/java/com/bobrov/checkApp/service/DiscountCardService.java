package com.bobrov.checkApp.service;

import com.bobrov.checkApp.model.DiscountCard;
import org.springframework.data.domain.Page;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public interface DiscountCardService {
    DiscountCard findById(@Min(1)Long id);

    Page<DiscountCard> findAll(@Min(0) Integer offset, @Min(1) Integer limit);

    DiscountCard save(@NotNull DiscountCard card);

    DiscountCard update(@Min(1) Long id, @NotNull DiscountCard card);

    void delete(@Min(1) Long id);
}
