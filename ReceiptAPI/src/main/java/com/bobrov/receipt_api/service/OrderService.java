package com.bobrov.receipt_api.service;

import com.bobrov.receipt_api.dto.OrderDto;
import com.bobrov.receipt_api.model.Order;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;

import javax.validation.Valid;
import javax.validation.constraints.Min;

public interface OrderService {
    Order findById(@Min(1) Long id);

    Page<Order> findAll(@Min(0) Integer offset, @Min(1) Integer limit);

    Order save(@Valid OrderDto orderDto);

    Order update(@Min(1) Long id, @Valid OrderDto orderDto);

    void deleteById(@Min(1) Long id);

    void makeReceipt(@Min(1) Long id);

    Resource load(@Min(1) Long id);
}
