package com.bobrov.checkApp.service;


import com.bobrov.checkApp.model.Product;
import org.springframework.data.domain.Page;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public interface ProductService {
    Product findById(@Min(1) Long id);

    Page<Product> findAll(@Min(0) Integer offset, @Min(1) Integer limit);

    Product save(@NotNull Product product);

    Product update(@Min(1) Long id, @NotNull Product product);

    void delete(@Min(1) Long id);
}
