package com.bobrov.checkApp.service;

import com.bobrov.checkApp.model.Order;

import java.util.List;

public interface OrderService {
    Order findById(Long id);

    List<Order> findAll(Integer offset, Integer limit);

    Order save(Order order);

    Order update(Order order);

    void delete(Long id);

    public void makeReceipt(Long id);
}
