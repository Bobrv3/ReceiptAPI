package com.bobrov.checkApp.service.impl;

import com.bobrov.checkApp.dao.ProductRepository;
import com.bobrov.checkApp.model.Product;
import com.bobrov.checkApp.service.ProductService;
import com.bobrov.checkApp.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

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
    public List<Product> findAll(@Min(0) Integer offset, @Min(1) Integer limit) {
        return repository.findAll(PageRequest.of(offset, limit)).getContent();
    }

    // ToDO Product validation
    @Override
    public Product save(@NotNull Product product) {
        return repository.save(product);
    }

    @Override
    public Product update(@NotNull Product product) {
        findById(product.getId());

        return repository.save(product);
    }

    @Override
    public void delete(@Min(1) Long id) {
        findById(id);

        repository.deleteById(id);
    }
}
