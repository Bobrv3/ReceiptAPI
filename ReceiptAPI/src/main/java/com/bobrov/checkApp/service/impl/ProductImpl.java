package com.bobrov.checkApp.service.impl;

import com.bobrov.checkApp.dao.ProductRepository;
import com.bobrov.checkApp.model.Product;
import com.bobrov.checkApp.service.ProductService;
import com.bobrov.checkApp.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor
@Validated
public class ProductImpl implements ProductService {
    private final ProductRepository repository;

    @Override
    public Product findById(@Min(1) Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Page<Product> findAll(@Min(0) Integer offset, @Min(1) Integer limit) {
        return repository.findAll(PageRequest.of(offset, limit));
    }

    // ToDO Product validation
    @Override
    @Transactional
    public Product save(@NotNull Product product) {
        return repository.save(product);
    }

    @Override
    @Transactional
    public Product update(@Min(1) Long id, @NotNull Product product) {
        findById(id);

        return repository.save(product);
    }

    @Override
    @Transactional
    public void delete(@Min(1) Long id) {
        Product product = findById(id);

        product.setStatus(Product.ProductStatus.DELETED);
    }
}
