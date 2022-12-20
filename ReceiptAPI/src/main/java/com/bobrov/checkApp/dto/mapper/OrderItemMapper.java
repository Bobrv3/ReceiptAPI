package com.bobrov.checkApp.dto.mapper;

import com.bobrov.checkApp.dto.OrderItemDto;
import com.bobrov.checkApp.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {ProductMapper.class})
public interface OrderItemMapper {
    OrderItemMapper INSTANCE = Mappers.getMapper(OrderItemMapper.class);

    OrderItemDto toDto(OrderItem detail);

    OrderItem toItem(OrderItemDto dto);
}
