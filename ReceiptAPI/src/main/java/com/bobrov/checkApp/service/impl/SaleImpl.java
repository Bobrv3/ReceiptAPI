package com.bobrov.checkApp.service.impl;

import com.bobrov.checkApp.dao.SaleRepository;
import com.bobrov.checkApp.dto.SaleDto;
import com.bobrov.checkApp.dto.mapper.SaleMapper;
import com.bobrov.checkApp.model.Sale;
import com.bobrov.checkApp.service.SaleService;
import com.bobrov.checkApp.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;

@Service
@RequiredArgsConstructor
@Validated
public class SaleImpl implements SaleService {
    private final SaleRepository repository;

    @Override
    public Sale findById(@Min(1) Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Page<Sale> findAll(@Min(0) Integer offset, @Min(1) Integer limit) {
        return repository.findAll(PageRequest.of(offset, limit));
    }

    @Override
    @Transactional
    public Sale save(@Valid SaleDto saleDto) {
        return repository.save(
                SaleMapper.INSTANCE.toModel(saleDto)
        );
    }

    @Override
    @Transactional
    public Sale update(@Min(1) Long id, @Valid SaleDto saleDto) {
        Sale sale = findById(id);
        SaleMapper.INSTANCE.updateModel(saleDto, sale);

        return repository.save(sale);
    }

    @Override
    @Transactional
    public void deleteById(@Min(1) Long id) {
        Sale sale = findById(id);

        sale.setStatus(Sale.SaleStatus.DELETED);
    }
}
