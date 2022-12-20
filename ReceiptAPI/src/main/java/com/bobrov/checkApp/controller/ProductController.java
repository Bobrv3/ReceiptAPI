package com.bobrov.checkApp.controller;

import com.bobrov.checkApp.dto.ProductDto;
import com.bobrov.checkApp.dto.mapper.ProductMapper;
import com.bobrov.checkApp.model.Product;
import com.bobrov.checkApp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private static final String ID_PATH_VARIABLE = "/{id:\\d+}";
    private final ProductService service;

    @GetMapping
    public Page<Product> getList(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return service.findAll(offset, limit);
    }

    @GetMapping(ID_PATH_VARIABLE)
    public ProductDto get(@PathVariable Long id) {
        return ProductMapper.INSTANCE.toDto(
                service.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody ProductDto productDto) {
        Product product = service.save(productDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(ProductMapper.INSTANCE.toDto(product));
    }

    @PutMapping(ID_PATH_VARIABLE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @RequestBody ProductDto productDto,
            @PathVariable Long id
    ) {
        service.update(id, productDto);
    }

    @DeleteMapping(ID_PATH_VARIABLE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
