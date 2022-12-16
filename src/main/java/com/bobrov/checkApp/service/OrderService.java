package com.bobrov.checkApp.service;

import com.bobrov.checkApp.model.Order;
import org.springframework.data.domain.Page;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public interface OrderService {
    Order findById(@Min(1) Long id);

    Page<Order> findAll(@Min(0) Integer offset, @Min(1) Integer limit);

    Order save(@NotNull Order order);

    Order update(@Min(1) Long id, @NotNull Order order);

    void delete(@Min(1) Long id);

    void makeReceipt(@Min(1) Long id);
}
