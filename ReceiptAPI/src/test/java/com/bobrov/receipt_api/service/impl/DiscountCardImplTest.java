package com.bobrov.receipt_api.service.impl;

import com.bobrov.receipt_api.dao.DiscountCardRepository;
import com.bobrov.receipt_api.dto.DiscountCardDto;
import com.bobrov.receipt_api.model.DiscountCard;
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
class DiscountCardImplTest {
    private DiscountCard card = DiscountCard.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .build();
    private DiscountCardDto cardDto = DiscountCardDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .status(DiscountCard.DiscountCardStatus.ACTIVE)
            .build();
    @Mock
    DiscountCardRepository repository;

    @InjectMocks
    DiscountCardImpl service;

    @Test
    @DisplayName("findById: success")
    void findById_success() {
        DiscountCard card = new DiscountCard();

        given(repository.findById(1L))
                .willReturn(Optional.of(card));

        assertEquals(card, service.findById(1L));
    }

    @Test
    @DisplayName("findById: discount card with such id doesn't exist")
    void should_throwNotFoundException_when_discountCardWithSuchIdIsNotFound() {
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

        PageImpl<DiscountCard> cards = new PageImpl<>(Collections.emptyList());
        given(repository.findAll(PageRequest.of(offset, limit)))
                .willReturn(cards);

        assertEquals(
                cards,
                service.findAll(offset, limit)
        );
    }

    @Test
    void saveSuccess() {
        given(repository.save(card))
                .willReturn(card);

        assertEquals(card, service.save(cardDto));
    }

    @Test
    void updateSuccess() {
        given(repository.findById(card.getId()))
                .willReturn(Optional.of(card));
        given(repository.save(card))
                .willReturn(card);

        assertEquals(card, service.update(cardDto.getId(), cardDto));
    }

    @Test
    void deleteByIdSuccess() {
        given(repository.findById(card.getId()))
                .willReturn(Optional.of(card));

        service.deleteById(card.getId());

        assertEquals(DiscountCard.DiscountCardStatus.DELETED, card.getStatus());

        card.setStatus(DiscountCard.DiscountCardStatus.ACTIVE);
    }

    @Test
    void should_throwNotFoundEx_when_deleteCardWithNotExistId() {
        Long id = card.getId();

        given(repository.findById(id))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Requested resource with id=1 was not found");
    }
}