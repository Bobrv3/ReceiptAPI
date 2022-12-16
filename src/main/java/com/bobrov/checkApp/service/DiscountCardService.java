package com.bobrov.checkApp.service;

import com.bobrov.checkApp.model.DiscountCard;
import org.springframework.data.domain.Page;

public interface DiscountCardService {
    DiscountCard findById(Long id);

    Page<DiscountCard> findAll(Integer offset, Integer limit);

    DiscountCard save(DiscountCard card);

    DiscountCard update(Long id, DiscountCard card);

    void delete(Long id);
}
