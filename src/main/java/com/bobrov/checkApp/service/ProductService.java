package com.bobrov.checkApp.service;


import com.bobrov.checkApp.model.Product;

import java.util.List;

public interface ProductService {
    Product findById(Long id);

    List<Product> findAll(Integer offset, Integer limit);

    Product save(Product product);

    Product update(Product product);

    void delete(Long id);
}
