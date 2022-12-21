package com.bobrov.receipt_api.controller;

import com.bobrov.receipt_api.dto.DiscountCardDto;
import com.bobrov.receipt_api.dto.mapper.DiscountCardMapper;
import com.bobrov.receipt_api.model.DiscountCard;
import com.bobrov.receipt_api.service.DiscountCardService;
import com.bobrov.receipt_api.service.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DiscountCardController.class)
class DiscountCardControllerTest {
    @MockBean
    private DiscountCardService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    private DiscountCard discountCardWithId1 = DiscountCard.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .build();
    private DiscountCardDto discountCardDtoWithId1 = DiscountCardDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .status(DiscountCard.DiscountCardStatus.ACTIVE)
            .build();
    private DiscountCardDto discountCardDtoWithoutId = DiscountCardDto.builder()
            .discountSize(BigDecimal.valueOf(10))
            .status(DiscountCard.DiscountCardStatus.ACTIVE)
            .build();

    @Test
    @DisplayName("getByID: success")
    void getById_Success() throws Exception {
        when(service.findById(1L))
                .thenReturn(discountCardWithId1);

        this.mockMvc.perform(get("/api/v1/discount-cards/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(discountCardDtoWithId1)));
    }

    @Test
    @DisplayName("getByID: discountCard with such id doesn't found")
    void should_returnNoSuchDiscountCardEx_if_discountCardNotFound() throws Exception {
        when(service.findById(10L))
                .thenThrow(new NotFoundException(10L));

        this.mockMvc.perform(get("/api/v1/discount-cards/10"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NotFoundException")))
                .andExpect(content().string(containsString("Requested resource with id=10 was not found")));
    }

    @Test
    @DisplayName("getByID: not valid id")
    void should_returnExceptionResponseWithMessageForId_if_idIsInvalid() throws Exception {
        when(service.findById(0L))
                .thenThrow(new ConstraintViolationException("id: must be greater than or equal to 1", null));

        this.mockMvc.perform(get("/api/v1/discount-cards/0"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("id: must be greater than or equal to 1")));
    }

    @Test
    @DisplayName("getByID: incorrect id")
    void should_returnNotFoundStatus_if_idIsIncorrect() throws Exception {
        this.mockMvc.perform(get("/api/v1/discount-cards/abc"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getAll: success")
    void getAllSuccess() throws Exception {
        int offset = 0;
        int limit = 20;

        PageImpl<DiscountCard> discountCards = new PageImpl<>(Collections.emptyList());
        when(service.findAll(offset, limit))
                .thenReturn(discountCards);

        this.mockMvc.perform(get("/api/v1/discount-cards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(discountCards)));
    }

    @Test
    @DisplayName("createDiscountCard: success")
    void createDiscountCard_success() throws Exception {
        when(service.save(discountCardDtoWithoutId))
                .thenAnswer(mock -> {
                    discountCardDtoWithoutId.setId(5L);
                    DiscountCard discountCard = DiscountCardMapper.INSTANCE.toModel(discountCardDtoWithoutId);

                    return discountCard;
                });

        this.mockMvc.perform(post("/api/v1/discount-cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discountCardDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/discount-cards/5"))
                .andExpect(content().json(objectMapper.writeValueAsString(discountCardDtoWithoutId)));
    }

    @Test
    @DisplayName("createDiscountCard: not valid discountCard")
    void should_returnConstraintViolationEx_if_discountCardIsNotValid() throws Exception {
        discountCardDtoWithoutId.setDiscountSize(BigDecimal.valueOf(-1.1245));

        when(service.save(discountCardDtoWithoutId))
                .thenThrow(new ConstraintViolationException("discountSize: must be greater than 0.0", null));

        this.mockMvc.perform(post("/api/v1/discount-cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discountCardDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("discountSize: must be greater than 0.0")));

        discountCardDtoWithoutId.setDiscountSize(BigDecimal.valueOf(10));
    }

    @Test
    @DisplayName("updateDiscountCard: success")
    void updateDiscountCard_success() throws Exception {
        when(service.update(4L, discountCardDtoWithId1))
                .thenAnswer(mock -> {
                    discountCardDtoWithId1.setId(4L);
                    DiscountCard discountCardWithId4 = DiscountCardMapper.INSTANCE.toModel(discountCardDtoWithId1);
                    discountCardDtoWithId1.setId(1L);

                    return discountCardWithId4;
                });

        this.mockMvc.perform(put("/api/v1/discount-cards/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discountCardDtoWithId1)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateDiscountCard: not valid id and discountCard")
    void should_returnConstraintViolationEx_if_IdOrDiscountCardIsIncorrect() throws Exception {
        discountCardDtoWithoutId.setDiscountSize(BigDecimal.valueOf(-1.1245));

        when(service.update(0L, discountCardDtoWithoutId))
                .thenThrow(new ConstraintViolationException("id: must be greater than or equal to 1, discountSize: must be greater than 0.0", null));

        this.mockMvc.perform(put("/api/v1/discount-cards/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discountCardDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("id: must be greater than or equal to 1")))
                .andExpect(content().string(containsString("discountSize: must be greater than 0.0")));

        discountCardDtoWithoutId.setDiscountSize(BigDecimal.valueOf(10));
    }

    @Test
    @DisplayName("updateDiscountCard: discountCard with such id doesn't exist")
    void should_returnNoSuchDiscountCardEx_if_updateAndDiscountCardIsNotFound() throws Exception {
        when(service.update(10L, discountCardDtoWithoutId))
                .thenThrow(new NotFoundException(10L));

        this.mockMvc.perform(put("/api/v1/discount-cards/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(discountCardDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NotFoundException")))
                .andExpect(content().string(containsString("Requested resource with id=10 was not found")));
    }

    @Test
    @DisplayName("deleteById: success")
    void deleteById() throws Exception {
        this.mockMvc.perform(delete("/api/v1/discount-cards/4"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service).deleteById(4L);
    }
}