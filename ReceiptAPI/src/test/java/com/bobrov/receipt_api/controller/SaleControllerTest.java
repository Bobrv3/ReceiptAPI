package com.bobrov.receipt_api.controller;

import com.bobrov.receipt_api.dto.SaleDto;
import com.bobrov.receipt_api.dto.mapper.SaleMapper;
import com.bobrov.receipt_api.model.Sale;
import com.bobrov.receipt_api.service.SaleService;
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

@WebMvcTest(controllers = SaleController.class)
class SaleControllerTest {
    @MockBean
    private SaleService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Sale saleWithId1 = Sale.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();
    private SaleDto saleDtoWithId1 = SaleDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .status(Sale.SaleStatus.ENABLE)
            .build();

    private SaleDto saleDtoWithoutId = SaleDto.builder()
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .status(Sale.SaleStatus.ENABLE)
            .build();

    @Test
    @DisplayName("getByID: success")
    void getById_Success() throws Exception {
        when(service.findById(1L))
                .thenReturn(saleWithId1);

        this.mockMvc.perform(get("/api/v1/sales/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(saleDtoWithId1)));
    }

    @Test
    @DisplayName("getByID: sale with such id doesn't found")
    void should_returnNoSuchSaleEx_if_saleNotFound() throws Exception {
        when(service.findById(10L))
                .thenThrow(new NotFoundException(10L));

        this.mockMvc.perform(get("/api/v1/sales/10"))
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

        this.mockMvc.perform(get("/api/v1/sales/0"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("id: must be greater than or equal to 1")));
    }

    @Test
    @DisplayName("getByID: incorrect id")
    void should_returnNotFoundStatus_if_idIsIncorrect() throws Exception {
        this.mockMvc.perform(get("/api/v1/sales/abc"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getAll: success")
    void getAllSuccess() throws Exception {
        int offset = 0;
        int limit = 20;

        PageImpl<Sale> sales = new PageImpl<>(Collections.emptyList());
        when(service.findAll(offset, limit))
                .thenReturn(sales);

        this.mockMvc.perform(get("/api/v1/sales"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sales)));
    }

    @Test
    @DisplayName("createSale: success")
    void createSale_success() throws Exception {
        when(service.save(saleDtoWithoutId))
                .thenAnswer(mock -> {
                    saleDtoWithoutId.setId(5L);
                    Sale sale = SaleMapper.INSTANCE.toModel(saleDtoWithoutId);

                    return sale;
                });

        this.mockMvc.perform(post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saleDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/sales/5"))
                .andExpect(content().json(objectMapper.writeValueAsString(saleDtoWithoutId)));
    }

    @Test
    @DisplayName("createSale: not valid sale")
    void should_returnConstraintViolationEx_if_saleIsNotValid() throws Exception {
        saleDtoWithoutId.setDiscountSize(BigDecimal.valueOf(-1.1245));

        when(service.save(saleDtoWithoutId))
                .thenThrow(new ConstraintViolationException("discountSize: must be greater than 0.0", null));

        this.mockMvc.perform(post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saleDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("discountSize: must be greater than 0.0")));

        saleDtoWithoutId.setDiscountSize(BigDecimal.valueOf(10));
    }

    @Test
    @DisplayName("updateSale: success")
    void updateSale_success() throws Exception {
        when(service.update(4L, saleDtoWithId1))
                .thenAnswer(mock -> {
                    saleDtoWithId1.setId(4L);
                    Sale saleWithId4 = SaleMapper.INSTANCE.toModel(saleDtoWithId1);
                    saleDtoWithId1.setId(1L);

                    return saleWithId4;
                });

        this.mockMvc.perform(put("/api/v1/sales/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saleDtoWithId1)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateSale: not valid id and sale")
    void should_returnConstraintViolationEx_if_IdOrSaleIsIncorrect() throws Exception {
        saleDtoWithoutId.setDiscountSize(BigDecimal.valueOf(-1.1245));

        when(service.update(0L, saleDtoWithoutId))
                .thenThrow(new ConstraintViolationException("id: must be greater than or equal to 1, discountSize: must be greater than 0.0", null));

        this.mockMvc.perform(put("/api/v1/sales/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saleDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("id: must be greater than or equal to 1")))
                .andExpect(content().string(containsString("discountSize: must be greater than 0.0")));

        saleDtoWithoutId.setDiscountSize(BigDecimal.valueOf(10));
    }

    @Test
    @DisplayName("updateSale: sale with such id doesn't exist")
    void should_returnNoSuchSaleEx_if_updateAndSaleIsNotFound() throws Exception {
        when(service.update(10L, saleDtoWithoutId))
                .thenThrow(new NotFoundException(10L));

        this.mockMvc.perform(put("/api/v1/sales/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saleDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NotFoundException")))
                .andExpect(content().string(containsString("Requested resource with id=10 was not found")));
    }

    @Test
    @DisplayName("deleteById: success")
    void deleteById() throws Exception {
        this.mockMvc.perform(delete("/api/v1/sales/4"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service).deleteById(4L);
    }
}