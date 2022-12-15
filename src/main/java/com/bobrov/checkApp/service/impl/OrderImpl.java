package com.bobrov.checkApp.service.impl;

import com.bobrov.checkApp.dao.OrderRepository;
import com.bobrov.checkApp.model.Order;
import com.bobrov.checkApp.service.OrderService;
import com.bobrov.checkApp.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderImpl implements OrderService {
    private OrderRepository repository;

    @Override
    public Order findById(@Min(1) Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public List<Order> findAll(@Min(0) Integer offset, @Min(1) Integer limit) {
        return repository.findAll(PageRequest.of(offset, limit)).getContent();
    }

    // ToDO order validation
    @Override
    public Order save(@NotNull Order order) {
        return repository.save(order);
    }

    @Override
    public Order update(@NotNull Order order) {
        findById(order.getId());

        return repository.save(order);
    }

    @Override
    public void delete(@Min(1) Long id) {
        findById(id);

        repository.deleteById(id);
    }
}
