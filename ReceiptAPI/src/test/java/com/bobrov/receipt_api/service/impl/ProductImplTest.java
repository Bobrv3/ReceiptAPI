package com.bobrov.receipt_api.service.impl;

import com.bobrov.receipt_api.dao.ProductRepository;
import com.bobrov.receipt_api.dto.ProductDto;
import com.bobrov.receipt_api.dto.SaleDto;
import com.bobrov.receipt_api.model.Product;
import com.bobrov.receipt_api.model.Sale;
import com.bobrov.receipt_api.service.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductImplTest {
    private Sale sale = Sale.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();

    private SaleDto saleDto = SaleDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();
    private Product product = Product.builder()
            .id(1L)
            .description("Milk")
            .price(BigDecimal.valueOf(1.6))
            .sale(sale)
            .build();
    private ProductDto productDto = ProductDto.builder()
            .id(1L)
            .description("Milk")
            .price(BigDecimal.valueOf(1.6))
            .sale(saleDto)
            .build();

    @Mock
    ProductRepository repository;

    @InjectMocks
    ProductImpl service;

    @Test
    @DisplayName("findById: success")
    void findById_success() {
        Product product = new Product();

        given(repository.findById(1L))
                .willReturn(Optional.of(product));

        assertEquals(product, service.findById(1L));
    }

    @Test
    @DisplayName("findById: discount product with such id doesn't exist")
    void should_throwNotFoundException_when_productWithSuchIdIsNotFound() {
        given(repository.findById(1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Requested resource with id=1 was not found");
    }

    @Test
    void findAllSuccess() {
        int offset = 0;
        int limit = 20;

        PageImpl<Product> products = new PageImpl<>(Collections.emptyList());
        given(repository.findAll(PageRequest.of(offset, limit)))
                .willReturn(products);

        assertEquals(
                products,
                service.findAll(offset, limit)
        );
    }

    @Test
    void saveSuccess() {
        given(repository.save(product))
                .willReturn(product);

        assertEquals(product, service.save(productDto));
    }

    @Test
    void updateSuccess() {
        given(repository.findById(product.getId()))
                .willReturn(Optional.of(product));
        given(repository.save(product))
                .willReturn(product);

        assertEquals(product, service.update(productDto.getId(), productDto));
    }

    @Test
    void deleteByIdSuccess() {
        given(repository.findById(product.getId()))
                .willReturn(Optional.of(product));

        service.deleteById(product.getId());

        assertEquals(Product.ProductStatus.DELETED, product.getStatus());

        product.setStatus(Product.ProductStatus.ENABLE);
    }

    @Test
    void should_throwNotFoundEx_when_deleteCardWithNotExistId() {
        Long id = product.getId();

        given(repository.findById(id))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Requested resource with id=1 was not found");
    }
}