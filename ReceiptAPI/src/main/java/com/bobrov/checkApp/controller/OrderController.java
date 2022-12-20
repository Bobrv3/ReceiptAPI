package com.bobrov.checkApp.controller;

import com.bobrov.checkApp.dto.OrderDto;
import com.bobrov.checkApp.dto.mapper.OrderMapper;
import com.bobrov.checkApp.model.Order;
import com.bobrov.checkApp.service.OrderService;
import com.bobrov.checkApp.service.exception.NotFoundException;
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
    private static final String ID_PATH_VARIABLE = "/{id:\\d+}";
    private final OrderService service;

    @GetMapping
    public Page<Order> getList(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return service.findAll(offset, limit);
    }

    @GetMapping(ID_PATH_VARIABLE)
    public OrderDto get(@PathVariable Long id) {
        return OrderMapper.INSTANCE.toDto(
                service.findById(id)
        );
    }

    @GetMapping("/{id:\\d+}/receipt")
    public ResponseEntity<Resource> getReceipt(@PathVariable Long id){
        service.makeReceipt(id);

        Resource file = service.load(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\""+file.getFilename()+"\"")
                .body(file);
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody OrderDto orderDto) {
        Order order = service.save(orderDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(order.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(OrderMapper.INSTANCE.toDto(order));
    }

    @PutMapping(ID_PATH_VARIABLE)
    public ResponseEntity<Object> update(
            @RequestBody OrderDto orderDto,
            @PathVariable Long id
    ) {
        try {
            service.findById(orderDto.getId());
        } catch (NotFoundException e) {
            return save(orderDto);
        }

        service.update(id, orderDto);
        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping(ID_PATH_VARIABLE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
