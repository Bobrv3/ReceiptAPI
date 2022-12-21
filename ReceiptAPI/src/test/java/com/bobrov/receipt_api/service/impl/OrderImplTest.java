package com.bobrov.receipt_api.service.impl;

import com.bobrov.receipt_api.dao.DiscountCardRepository;
import com.bobrov.receipt_api.dao.OrderRepository;
import com.bobrov.receipt_api.dao.ProductRepository;
import com.bobrov.receipt_api.dto.OrderDto;
import com.bobrov.receipt_api.dto.mapper.OrderMapper;
import com.bobrov.receipt_api.model.DiscountCard;
import com.bobrov.receipt_api.model.Order;
import com.bobrov.receipt_api.model.OrderItem;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OrderImplTest {
    private static final String FILE_PATH = "%s/%s.txt";
    private static final String DIRECTORY_PATH = "./receipts";

    private Sale sale = Sale.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(10))
            .fromQuantity(5)
            .build();
    private DiscountCard card = DiscountCard.builder()
            .id(1L)
            .discountSize(BigDecimal.valueOf(5))
            .build();
    private Product product = Product.builder()
            .id(1L)
            .description("Milk")
            .price(BigDecimal.valueOf(1.6))
            .sale(sale)
            .build();
    private OrderItem item = OrderItem.builder()
            .product(product)
            .quantity(6)
            .build();
    private Order order = Order.builder()
            .id(1L)
            .discountCard(card)
            .build();

    public OrderImplTest() {
        order.addItem(item);
    }

    @Mock
    OrderRepository orderRepository;
    @Mock
    ProductRepository productRepository;
    @Mock
    DiscountCardRepository discountCardRepository;
    @InjectMocks
    OrderImpl service;

    @Test
    @DisplayName("makeReceipt: order has sale product and discount card")
    void should_writeSaleDiscountAndCardDiscount_if_orderHasСardAndOrderOnSale() throws IOException {
        long orderId = 1L;

        given(orderRepository.findById(orderId))
                .willReturn(Optional.of(order));

        service.makeReceipt(orderId);

        String receipt = Files.readAllLines(Paths.get(String.format(FILE_PATH, DIRECTORY_PATH, orderId))).stream()
                .collect(Collectors.joining());

        assertAll(
                () -> assertTrue(Files.exists(Paths.get(String.format(FILE_PATH, DIRECTORY_PATH, orderId)))),
                () -> assertTrue(receipt.contains("discount = $0.96")),
                () -> assertTrue(receipt.contains("CARD DISCOUNT                   $0.48")),
                () -> assertTrue(receipt.contains("TOTAL DISCOUNT                  $1.44")),
                () -> assertTrue(receipt.contains("TOTAL                           $8.16"))
        );
    }

    @Test
    @DisplayName("makeReceipt: order doesn't have sale product and discount card")
    void should_writeReceiptWithoutDiscount_if_orderDoesntHaveСardAndOrderOnSale() throws IOException {
        order.setDiscountCard(null);
        item.setQuantity(3);

        long orderId = 1L;
        given(orderRepository.findById(orderId))
                .willReturn(Optional.of(order));

        service.makeReceipt(orderId);

        String receipt = Files.readAllLines(Paths.get(String.format(FILE_PATH, DIRECTORY_PATH, orderId))).stream()
                .collect(Collectors.joining());

        assertAll(
                () -> assertTrue(Files.exists(Paths.get(String.format(FILE_PATH, DIRECTORY_PATH, orderId)))),
                () -> assertFalse(receipt.contains("discount =")),
                () -> assertTrue(receipt.contains("CARD DISCOUNT                   $0"))
        );

        order.setDiscountCard(card);
        item.setQuantity(6);
    }

    @Test
    @DisplayName("load: receipt exists")
    void should_returnResource_if_exists() {
        long orderId = 1L;
        given(orderRepository.findById(orderId))
                .willReturn(Optional.of(order));

        service.makeReceipt(orderId);

        assertDoesNotThrow(() -> service.load(orderId));
    }

    @Test
    @DisplayName("load: receipt doesn't exist")
    void should_throwEx_if_resourceDoesntExist() {
        long orderId = 1L;
        given(orderRepository.findById(orderId))
                .willReturn(Optional.of(order));

        service.makeReceipt(orderId);

        assertThatThrownBy(() -> service.load(2L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Could not read the file.");
    }

    @Test
    @DisplayName("findById: success")
    void findById_success() {
        Order order = new Order();

        given(orderRepository.findById(1L))
                .willReturn(Optional.of(order));

        assertEquals(order, service.findById(1L));
    }

    @Test
    @DisplayName("findById: discount order with such id doesn't exist")
    void should_throwNotFoundException_when_orderWithSuchIdIsNotFound() {
        given(orderRepository.findById(1L))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Requested resource with id=1 was not found");
    }

    @Test
    void findAllSuccess() {
        int offset = 0;
        int limit = 20;

        PageImpl<Order> orders = new PageImpl<>(Collections.emptyList());
        given(orderRepository.findAll(PageRequest.of(offset, limit)))
                .willReturn(orders);

        assertEquals(
                orders,
                service.findAll(offset, limit)
        );
    }

    @Test
    void saveSuccess() {
        Order order1 = Order.builder()
                .items(List.of(item))
                .discountCard(card)
                .build();

        doReturn(Optional.ofNullable(product))
                .when(productRepository).findById(product.getId());
        doReturn(Optional.ofNullable(card))
                .when(discountCardRepository).findById(card.getId());
        doReturn(order)
                .when(orderRepository).save(order1);

        OrderDto orderDto = OrderMapper.INSTANCE.toDto(order1);
        assertEquals(order, service.save(orderDto));
    }

    @Test
    void updateSuccess() {
        given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));
        given(orderRepository.save(order))
                .willReturn(order);
        doReturn(Optional.ofNullable(product))
                .when(productRepository).findById(product.getId());
        doReturn(Optional.ofNullable(card))
                .when(discountCardRepository).findById(card.getId());

        OrderDto orderDto = OrderMapper.INSTANCE.toDto(order);
        assertEquals(order, service.update(orderDto.getId(), orderDto));
    }

    @Test
    void deleteByIdSuccess() {
        given(orderRepository.findById(order.getId()))
                .willReturn(Optional.of(order));

        service.deleteById(order.getId());

        assertEquals(Order.OrderStatus.DELETED, order.getStatus());

        order.setStatus(Order.OrderStatus.ENABLE);
    }

    @Test
    void should_throwNotFoundEx_when_deleteCardWithNotExistId() {
        Long id = order.getId();

        given(orderRepository.findById(id))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Requested resource with id=1 was not found");
    }
}