package com.bobrov.checkApp.dto.mapper;

import com.bobrov.checkApp.dto.DiscountCardDto;
import com.bobrov.checkApp.model.DiscountCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface DiscountCardMapper {
    DiscountCardMapper INSTANCE = Mappers.getMapper(DiscountCardMapper.class);

    @Mapping(target = "status", defaultExpression = "java(DiscountCard.DiscountCardStatus.ACTIVE)")
    DiscountCard toModel(DiscountCardDto discountCardDto);
    DiscountCardDto toDto(DiscountCard discountCard);
    void updateModel(DiscountCardDto discountCardDto, @MappingTarget DiscountCard discountCard);
    List<DiscountCardDto> toListDto(List<DiscountCard> byId);
}
