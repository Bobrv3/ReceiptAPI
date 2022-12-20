package com.bobrov.receipt_api.dto.mapper;

import com.bobrov.receipt_api.dto.OrderDto;
import com.bobrov.receipt_api.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = OrderItemMapper.class)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDto toDto(Order order);

    Order toModel(OrderDto orderDto);

    List<OrderDto> toListDto(List<Order> orders);

    void updateModel(OrderDto orderDto, @MappingTarget Order order);
}
