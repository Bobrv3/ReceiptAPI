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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderImpl implements OrderService {
    private static final String FILE_PATH = "%s/receipt%s.txt";
    private static final String DIRECTORY_PATH = "./receipts";
    private final OrderRepository repository;

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

    @Override
    public void makeReceipt(Long id) {
        //ToDO coorect receipt out info

        try {
            Files.createDirectories(Paths.get(DIRECTORY_PATH));

            FileWriter writer = new FileWriter(String.format(FILE_PATH, DIRECTORY_PATH, id));
            writer.write("hello world");
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
