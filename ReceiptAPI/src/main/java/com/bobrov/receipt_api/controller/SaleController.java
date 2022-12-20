package com.bobrov.receipt_api.controller;

import com.bobrov.receipt_api.dto.SaleDto;
import com.bobrov.receipt_api.dto.mapper.SaleMapper;
import com.bobrov.receipt_api.model.Sale;
import com.bobrov.receipt_api.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {
    private static final String ID_PATH_VARIABLE = "/{id:\\d+}";
    private final SaleService service;

    @GetMapping
    public Page<Sale> getList(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return service.findAll(offset, limit);
    }

    @GetMapping(ID_PATH_VARIABLE)
    public SaleDto get(@PathVariable Long id) {
        return SaleMapper.INSTANCE.toDto(
                service.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody SaleDto saleDto) {
        Sale sale = service.save(saleDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(sale.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(SaleMapper.INSTANCE.toDto(sale));
    }

    @PutMapping(ID_PATH_VARIABLE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @RequestBody SaleDto saleDto,
            @PathVariable Long id
    ) {
        service.update(id, saleDto);
    }

    @DeleteMapping(ID_PATH_VARIABLE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
