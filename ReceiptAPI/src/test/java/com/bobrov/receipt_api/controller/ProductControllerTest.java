package com.bobrov.receipt_api.controller;

import com.bobrov.receipt_api.dto.ProductDto;
import com.bobrov.receipt_api.dto.SaleDto;
import com.bobrov.receipt_api.dto.mapper.ProductMapper;
import com.bobrov.receipt_api.model.Product;
import com.bobrov.receipt_api.model.Sale;
import com.bobrov.receipt_api.service.ProductService;
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

@WebMvcTest(controllers = ProductController.class)
class ProductControllerTest {
    @MockBean
    private ProductService service;

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

    private static final String MILK = "Milk";
    private Product productWithId1 = Product.builder()
            .id(1L)
            .price(BigDecimal.valueOf(10))
            .description(MILK)
            .sale(saleWithId1)
            .build();
    private ProductDto productDtoWithId1 = ProductDto.builder()
            .id(1L)
            .price(BigDecimal.valueOf(10))
            .sale(saleDtoWithId1)
            .description("Milk")
            .status(Product.ProductStatus.ENABLE)
            .build();

    private ProductDto productDtoWithoutId = ProductDto.builder()
            .price(BigDecimal.valueOf(10))
            .sale(saleDtoWithId1)
            .description("Milk")
            .status(Product.ProductStatus.ENABLE)
            .build();

    @Test
    @DisplayName("getByID: success")
    void getById_Success() throws Exception {
        when(service.findById(1L))
                .thenReturn(productWithId1);

        this.mockMvc.perform(get("/api/v1/products/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(productDtoWithId1)));
    }

    @Test
    @DisplayName("getByID: product with such id doesn't found")
    void should_returnNoSuchProductEx_if_productNotFound() throws Exception {
        when(service.findById(10L))
                .thenThrow(new NotFoundException(10L));

        this.mockMvc.perform(get("/api/v1/products/10"))
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

        this.mockMvc.perform(get("/api/v1/products/0"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("id: must be greater than or equal to 1")));
    }

    @Test
    @DisplayName("getByID: incorrect id")
    void should_returnNotFoundStatus_if_idIsIncorrect() throws Exception {
        this.mockMvc.perform(get("/api/v1/products/abc"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getAll: success")
    void getAllSuccess() throws Exception {
        int offset = 0;
        int limit = 20;

        PageImpl<Product> products = new PageImpl<>(Collections.emptyList());
        when(service.findAll(offset, limit))
                .thenReturn(products);

        this.mockMvc.perform(get("/api/v1/products"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(products)));
    }

    @Test
    @DisplayName("createProduct: success")
    void createProduct_success() throws Exception {
        when(service.save(productDtoWithoutId))
                .thenAnswer(mock -> {
                    productDtoWithoutId.setId(5L);
                    Product product = ProductMapper.INSTANCE.toModel(productDtoWithoutId);

                    return product;
                });

        this.mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/products/5"))
                .andExpect(content().json(objectMapper.writeValueAsString(productDtoWithoutId)));
    }

    @Test
    @DisplayName("createProduct: not valid product")
    void should_returnConstraintViolationEx_if_productIsNotValid() throws Exception {
        productDtoWithoutId.setPrice(BigDecimal.valueOf(-1.1245));

        when(service.save(productDtoWithoutId))
                .thenThrow(new ConstraintViolationException("discountSize: must be greater than 0.0", null));

        this.mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("discountSize: must be greater than 0.0")));

        productDtoWithoutId.setPrice(BigDecimal.valueOf(10));
    }

    @Test
    @DisplayName("updateProduct: success")
    void updateProduct_success() throws Exception {
        when(service.update(4L, productDtoWithId1))
                .thenAnswer(mock -> {
                    productDtoWithId1.setId(4L);
                    Product productWithId4 = ProductMapper.INSTANCE.toModel(productDtoWithId1);
                    productDtoWithId1.setId(1L);

                    return productWithId4;
                });

        this.mockMvc.perform(put("/api/v1/products/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDtoWithId1)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateProduct: not valid id and product")
    void should_returnConstraintViolationEx_if_IdOrProductIsIncorrect() throws Exception {
        productDtoWithoutId.setPrice(BigDecimal.valueOf(-1.1245));

        when(service.update(0L, productDtoWithoutId))
                .thenThrow(new ConstraintViolationException("id: must be greater than or equal to 1, discountSize: must be greater than 0.0", null));

        this.mockMvc.perform(put("/api/v1/products/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("id: must be greater than or equal to 1")))
                .andExpect(content().string(containsString("discountSize: must be greater than 0.0")));

        productDtoWithoutId.setPrice(BigDecimal.valueOf(10));
    }

    @Test
    @DisplayName("updateProduct: product with such id doesn't exist")
    void should_returnNoSuchProductEx_if_updateAndProductIsNotFound() throws Exception {
        when(service.update(10L, productDtoWithoutId))
                .thenThrow(new NotFoundException(10L));

        this.mockMvc.perform(put("/api/v1/products/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NotFoundException")))
                .andExpect(content().string(containsString("Requested resource with id=10 was not found")));
    }

    @Test
    @DisplayName("deleteById: success")
    void deleteById() throws Exception {
        this.mockMvc.perform(delete("/api/v1/products/4"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service).deleteById(4L);
    }
}