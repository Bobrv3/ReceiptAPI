package com.bobrov.receipt_api.service;


import com.bobrov.receipt_api.dto.ProductDto;
import com.bobrov.receipt_api.model.Product;
import org.springframework.data.domain.Page;

import javax.validation.Valid;
import javax.validation.constraints.Min;

public interface ProductService {
    Product findById(@Min(1) Long id);

    Page<Product> findAll(@Min(0) Integer offset, @Min(1) Integer limit);

    Product save(@Valid ProductDto productDto);

    Product update(@Min(1) Long id, @Valid ProductDto productDto);

    void deleteById(@Min(1) Long id);
}
