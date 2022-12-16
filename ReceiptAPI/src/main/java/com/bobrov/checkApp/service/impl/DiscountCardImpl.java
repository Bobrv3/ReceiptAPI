package com.bobrov.checkApp.service.impl;

import com.bobrov.checkApp.dao.DiscountCardRepository;
import com.bobrov.checkApp.model.DiscountCard;
import com.bobrov.checkApp.service.DiscountCardService;
import com.bobrov.checkApp.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor
public class DiscountCardImpl implements DiscountCardService {
    private final DiscountCardRepository repository;

    @Override
    public DiscountCard findById(@Min(1) Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Page<DiscountCard> findAll(@Min(0) Integer offset, @Min(1) Integer limit) {
        return repository.findAll(PageRequest.of(offset, limit));
    }

    // ToDO DiscountCard validation
    @Override
    @Transactional
    public DiscountCard save(@NotNull DiscountCard card) {
        return repository.save(card);
    }

    @Override
    @Transactional
    public DiscountCard update(@Min(1) Long id, @NotNull DiscountCard card) {
        findById(id);

        return repository.save(card);
    }

    @Override
    @Transactional
    public void delete(@Min(1) Long id) {
        DiscountCard card = findById(id);

        card.setStatus(DiscountCard.DiscountCardStatus.DELETED);
    }
}
 