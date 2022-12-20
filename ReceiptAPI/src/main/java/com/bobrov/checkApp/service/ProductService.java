package com.bobrov.checkApp.service;


import com.bobrov.checkApp.dto.ProductDto;
import com.bobrov.checkApp.model.Product;
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
