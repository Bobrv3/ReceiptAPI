package com.bobrov.checkApp.service.impl;

import com.bobrov.checkApp.dao.ProductRepository;
import com.bobrov.checkApp.dto.ProductDto;
import com.bobrov.checkApp.dto.mapper.ProductMapper;
import com.bobrov.checkApp.model.Product;
import com.bobrov.checkApp.service.ProductService;
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

    @Override
    @Transactional
    public Product save(@Valid ProductDto productDto) {
        return repository.save(
                ProductMapper.INSTANCE.toModel(productDto)
        );
    }

    @Override
    @Transactional
    public Product update(@Min(1) Long id, @Valid ProductDto productDto) {
        Product product = findById(id);
        ProductMapper.INSTANCE.updateModel(productDto, product);

        return repository.save(product);
    }

    @Override
    @Transactional
    public void deleteById(@Min(1) Long id) {
        Product product = findById(id);

        product.setStatus(Product.ProductStatus.DELETED);
    }
}
