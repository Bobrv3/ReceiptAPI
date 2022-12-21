package com.bobrov.receipt_api.controller;

import com.bobrov.receipt_api.dto.DiscountCardDto;
import com.bobrov.receipt_api.dto.OrderDto;
import com.bobrov.receipt_api.dto.OrderItemDto;
import com.bobrov.receipt_api.dto.ProductDto;
import com.bobrov.receipt_api.dto.SaleDto;
import com.bobrov.receipt_api.dto.mapper.OrderMapper;
import com.bobrov.receipt_api.model.DiscountCard;
import com.bobrov.receipt_api.model.Order;
import com.bobrov.receipt_api.model.OrderItem;
import com.bobrov.receipt_api.model.Product;
import com.bobrov.receipt_api.model.Sale;
import com.bobrov.receipt_api.service.OrderService;
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
import java.util.ArrayList;
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

@WebMvcTest(controllers = OrderController.class)
class OrderControllerTest {
    @MockBean
    private OrderService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String MILK = "Milk";
    private Sale saleWithId1 = Sale.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();

    private Product product = Product.builder()
            .id(1L)
            .description("Milk")
            .price(BigDecimal.valueOf(1.6))
            .sale(saleWithId1)
            .build();
    private DiscountCard card = DiscountCard.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(5))
            .build();
    private DiscountCardDto cardDto = DiscountCardDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(5))
            .status(DiscountCard.DiscountCardStatus.ACTIVE)
            .build();
    private SaleDto saleDtoWithId1 = SaleDto.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .status(Sale.SaleStatus.ENABLE)
            .build();
    private ProductDto productDto = ProductDto.builder()
            .id(1L)
            .description("Milk")
            .price(BigDecimal.valueOf(1.6))
            .sale(saleDtoWithId1)
            .status(Product.ProductStatus.ENABLE)
            .build();
    private ProductDto productDtoPriceIsNegative = ProductDto.builder()
            .id(1L)
            .description("Milk")
            .price(BigDecimal.valueOf(-1.1245))
            .sale(saleDtoWithId1)
            .status(Product.ProductStatus.ENABLE)
            .build();

    private OrderItem item = OrderItem.builder()
            .product(product)
            .quantity(6)
            .build();
    private OrderItemDto itemDto = OrderItemDto.builder()
            .product(productDto)
            .quantity(6)
            .build();
    private Order orderWithId1 = Order.builder()
            .id(1L)
            .discountCard(card)
            .status(Order.OrderStatus.ENABLE)
            .build();
    private OrderDto orderDtoWithId1 = OrderDto.builder()
            .id(1L)
            .items(new ArrayList<>())
            .discountCard(cardDto)
            .status(Order.OrderStatus.ENABLE)
            .build();

    private OrderDto orderDtoWithoutId = OrderDto.builder()
            .items(new ArrayList<>())
            .items(new ArrayList<>())
            .status(Order.OrderStatus.ENABLE)
            .build();

    public OrderControllerTest() {
        orderWithId1.addItem(item);
        orderDtoWithId1.getItems().add(itemDto);
        orderDtoWithoutId.getItems().add(itemDto);
    }

    @Test
    @DisplayName("getByID: success")
    void getById_Success() throws Exception {
        when(service.findById(1L))
                .thenReturn(orderWithId1);

        this.mockMvc.perform(get("/api/v1/orders/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orderDtoWithId1)));
    }

    @Test
    @DisplayName("getByID: order with such id doesn't found")
    void should_returnNoSuchOrderEx_if_orderNotFound() throws Exception {
        when(service.findById(10L))
                .thenThrow(new NotFoundException(10L));

        this.mockMvc.perform(get("/api/v1/orders/10"))
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

        this.mockMvc.perform(get("/api/v1/orders/0"))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("id: must be greater than or equal to 1")));
    }

    @Test
    @DisplayName("getByID: incorrect id")
    void should_returnNotFoundStatus_if_idIsIncorrect() throws Exception {
        this.mockMvc.perform(get("/api/v1/orders/abc"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getAll: success")
    void getAllSuccess() throws Exception {
        int offset = 0;
        int limit = 20;

        PageImpl<Order> orders = new PageImpl<>(Collections.emptyList());
        when(service.findAll(offset, limit))
                .thenReturn(orders);

        this.mockMvc.perform(get("/api/v1/orders"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orders)));
    }

    @Test
    @DisplayName("createOrder: success")
    void createOrder_success() throws Exception {
        when(service.save(orderDtoWithoutId))
                .thenAnswer(mock -> {
                    orderDtoWithoutId.setId(5L);
                    Order order = OrderMapper.INSTANCE.toModel(orderDtoWithoutId);

                    return order;
                });

        this.mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/orders/5"))
                .andExpect(content().json(objectMapper.writeValueAsString(orderDtoWithoutId)));
    }

    @Test
    @DisplayName("createOrder: not valid order")
    void should_returnConstraintViolationEx_if_orderIsNotValid() throws Exception {
        itemDto.setProduct(productDtoPriceIsNegative);

        when(service.save(orderDtoWithoutId))
                .thenThrow(new ConstraintViolationException("price: must be greater than 0.0", null));

        this.mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("price: must be greater than 0.0")));

        itemDto.setProduct(productDto);
    }

    @Test
    @DisplayName("updateOrder: success")
    void updateOrder_success() throws Exception {
        when(service.update(4L, orderDtoWithId1))
                .thenAnswer(mock -> {
                    orderDtoWithId1.setId(4L);
                    Order orderWithId4 = OrderMapper.INSTANCE.toModel(orderDtoWithId1);
                    orderDtoWithId1.setId(1L);

                    return orderWithId4;
                });

        this.mockMvc.perform(put("/api/v1/orders/4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDtoWithId1)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("updateOrder: not valid id and order")
    void should_returnConstraintViolationEx_if_IdOrOrderIsIncorrect() throws Exception {
        itemDto.setProduct(productDtoPriceIsNegative);

        when(service.update(0L, orderDtoWithoutId))
                .thenThrow(new ConstraintViolationException("id: must be greater than or equal to 1, price: must be greater than 0.0", null));

        this.mockMvc.perform(put("/api/v1/orders/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ConstraintViolationException")))
                .andExpect(content().string(containsString("id: must be greater than or equal to 1")))
                .andExpect(content().string(containsString("price: must be greater than 0.0")));

        itemDto.setProduct(productDto);
    }

    @Test
    @DisplayName("updateOrder: order with such id doesn't exist")
    void should_returnNoSuchOrderEx_if_updateAndOrderIsNotFound() throws Exception {
        when(service.update(10L, orderDtoWithoutId))
                .thenThrow(new NotFoundException(10L));

        this.mockMvc.perform(put("/api/v1/orders/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderDtoWithoutId)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("NotFoundException")))
                .andExpect(content().string(containsString("Requested resource with id=10 was not found")));
    }

    @Test
    @DisplayName("deleteById: success")
    void deleteById() throws Exception {
        this.mockMvc.perform(delete("/api/v1/orders/4"))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(service).deleteById(4L);
    }
}