package com.bobrov.checkApp.controller;

import com.bobrov.checkApp.dto.DiscountCardDto;
import com.bobrov.checkApp.dto.mapper.DiscountCardMapper;
import com.bobrov.checkApp.model.DiscountCard;
import com.bobrov.checkApp.service.DiscountCardService;
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
@RequestMapping("/api/v1/discount-cards")
@RequiredArgsConstructor
public class DiscountCardController {
    private static final String ID_PATH_VARIABLE = "/{id:\\d+}";
    private final DiscountCardService service;

    @GetMapping
    public Page<DiscountCard> getList(
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        return service.findAll(offset, limit);
    }

    @GetMapping(ID_PATH_VARIABLE)
    public DiscountCardDto get(@PathVariable Long id) {
        return DiscountCardMapper.INSTANCE.toDto(
                service.findById(id)
        );
    }

    @PostMapping
    public ResponseEntity<Object> save(@RequestBody DiscountCardDto cardDto) {
        DiscountCard card = service.save(cardDto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(card.getId())
                .toUri();

        return ResponseEntity
                .created(uri)
                .body(DiscountCardMapper.INSTANCE.toDto(card));
    }

    @PutMapping(ID_PATH_VARIABLE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @RequestBody DiscountCardDto cardDto,
            @PathVariable Long id
    ) {
        service.update(id, cardDto);
    }

    @DeleteMapping(ID_PATH_VARIABLE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
