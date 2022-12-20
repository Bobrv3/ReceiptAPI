package com.bobrov.receipt_api.dto.mapper;

import com.bobrov.receipt_api.dto.DiscountCardDto;
import com.bobrov.receipt_api.model.DiscountCard;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DiscountCardMapper {
    DiscountCardMapper INSTANCE = Mappers.getMapper(DiscountCardMapper.class);

    DiscountCard toModel(DiscountCardDto discountCardDto);
    DiscountCardDto toDto(DiscountCard discountCard);
    void updateModel(DiscountCardDto discountCardDto, @MappingTarget DiscountCard discountCard);
    List<DiscountCardDto> toListDto(List<DiscountCard> byId);
}
