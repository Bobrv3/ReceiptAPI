package com.bobrov.receipt_api.service.impl;

import com.bobrov.receipt_api.dao.SaleRepository;
import com.bobrov.receipt_api.dto.SaleDto;
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
class SaleImplTest {
    private Sale sale = Sale.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();
    private SaleDto saleDto = SaleDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .status(Sale.SaleStatus.ENABLE)
            .build();

    @Mock
    SaleRepository repository;

    @InjectMocks
    SaleImpl service;

    @Test
    @DisplayName("findById: success")
    void findById_success() {
        Sale sale = new Sale();

        given(repository.findById(1L))
                .willReturn(Optional.of(sale));

        assertEquals(sale, service.findById(1L));
    }

    @Test
    @DisplayName("findById: discount sale with such id doesn't exist")
    void should_throwNotFoundException_when_saleWithSuchIdIsNotFound() {
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

        PageImpl<Sale> sales = new PageImpl<>(Collections.emptyList());
        given(repository.findAll(PageRequest.of(offset, limit)))
                .willReturn(sales);

        assertEquals(
                sales,
                service.findAll(offset, limit)
        );
    }

    @Test
    void saveSuccess() {
        given(repository.save(sale))
                .willReturn(sale);

        assertEquals(sale, service.save(saleDto));
    }

    @Test
    void updateSuccess() {
        given(repository.findById(sale.getId()))
                .willReturn(Optional.of(sale));
        given(repository.save(sale))
                .willReturn(sale);

        assertEquals(sale, service.update(saleDto.getId(), saleDto));
    }

    @Test
    void deleteByIdSuccess() {
        given(repository.findById(sale.getId()))
                .willReturn(Optional.of(sale));

        service.deleteById(sale.getId());

        assertEquals(Sale.SaleStatus.DELETED, sale.getStatus());

        sale.setStatus(Sale.SaleStatus.ENABLE);
    }

    @Test
    void should_throwNotFoundEx_when_deleteCardWithNotExistId() {
        Long id = sale.getId();

        given(repository.findById(id))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Requested resource with id=1 was not found");
    }
}