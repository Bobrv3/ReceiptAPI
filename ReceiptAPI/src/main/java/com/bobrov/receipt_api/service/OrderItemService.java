package com.bobrov.receipt_api.service;

import com.bobrov.receipt_api.model.OrderItem;

import java.util.List;

public interface OrderItemService {
    OrderItem findById(Long id);

    List<OrderItem> findAll(Integer offset, Integer limit);

    OrderItem save(OrderItem orderItem);

    OrderItem update(OrderItem orderItem);

    void delete(Long id);
}
