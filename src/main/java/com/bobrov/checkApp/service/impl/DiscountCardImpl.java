package com.bobrov.checkApp.service.impl;

import com.bobrov.checkApp.dao.DiscountCardRepository;
import com.bobrov.checkApp.model.DiscountCard;
import com.bobrov.checkApp.service.DiscountCardService;
import com.bobrov.checkApp.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountCardImpl implements DiscountCardService {
    private DiscountCardRepository repository;

    @Override
    public DiscountCard findById(@Min(1) Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public List<DiscountCard> findAll(@Min(0) Integer offset, @Min(1) Integer limit) {
        return repository.findAll(PageRequest.of(offset, limit)).getContent();
    }

    // ToDO DiscountCard validation
    @Override
    public DiscountCard save(@NotNull DiscountCard card) {
        return repository.save(card);
    }

    @Override
    public DiscountCard update(@NotNull DiscountCard card) {
        findById(card.getId());

        return repository.save(card);
    }

    @Override
    public void delete(@Min(1) Long id) {
        findById(id);

        repository.deleteById(id);
    }
}
 