package com.bobrov.checkApp.service;

import com.bobrov.checkApp.dto.SaleDto;
import com.bobrov.checkApp.model.Sale;
import org.springframework.data.domain.Page;

import javax.validation.Valid;
import javax.validation.constraints.Min;

public interface SaleService {
    Sale findById(@Min(1) Long id);

    Page<Sale> findAll(@Min(0) Integer offset, @Min(1) Integer limit);

    Sale save(@Valid SaleDto saleDto);

    Sale update(@Min(1) Long id, @Valid SaleDto saleDto);

    void deleteById(@Min(1) Long id);
}
