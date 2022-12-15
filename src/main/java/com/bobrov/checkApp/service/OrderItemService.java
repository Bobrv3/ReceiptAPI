package com.bobrov.checkApp.service;

import com.bobrov.checkApp.model.OrderItem;

import java.util.List;

public interface OrderItemService {
    OrderItem findById(Long id);

    List<OrderItem> findAll(Integer offset, Integer limit);

    OrderItem save(OrderItem orderItem);

    OrderItem update(OrderItem orderItem);

    void delete(Long id);
}
