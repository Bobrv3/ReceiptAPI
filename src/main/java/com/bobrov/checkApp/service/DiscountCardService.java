package com.bobrov.checkApp.service;

import com.bobrov.checkApp.model.DiscountCard;

import java.util.List;

public interface DiscountCardService {
    DiscountCard findById(Long id);

    List<DiscountCard> findAll(Integer offset, Integer limit);

    DiscountCard save(DiscountCard card);

    DiscountCard update(DiscountCard card);

    void delete(Long id);
}
