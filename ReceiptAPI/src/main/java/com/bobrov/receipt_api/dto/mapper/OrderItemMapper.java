package com.bobrov.receipt_api.dto.mapper;

import com.bobrov.receipt_api.dto.OrderItemDto;
import com.bobrov.receipt_api.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ProductMapper.class})
public interface OrderItemMapper {
    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    OrderItemDto toDto(OrderItem detail);

    OrderItem toItem(OrderItemDto dto);
}
