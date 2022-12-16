package com.bobrov.checkApp.controller;

import com.bobrov.checkApp.model.Order;
import com.bobrov.checkApp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @GetMapping
    public Page<Order> getList(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return service.findAll(offset, limit);
    }

    @GetMapping("/{id:\\d+}")
    public Order get(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/{id:\\d+}/receipt")
    public ResponseEntity<Resource> getReceipt(@PathVariable Long id){
        service.makeReceipt(id);

        Resource file = service.load(id.toString());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody Order order) {
        Order savedOrder = service.save(order);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(savedOrder);
    }

    @PutMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @RequestBody Order order,
            @PathVariable Long id
    ) {
        service.update(id, order);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
