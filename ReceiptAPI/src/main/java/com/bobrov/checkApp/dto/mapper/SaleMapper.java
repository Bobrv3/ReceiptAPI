package com.bobrov.checkApp.dto.mapper;

import com.bobrov.checkApp.dto.SaleDto;
import com.bobrov.checkApp.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = SaleMapper.class)
public interface SaleMapper {
    SaleMapper INSTANCE = Mappers.getMapper(SaleMapper.class);

    SaleDto toDto(Sale sale);

    Sale toModel(SaleDto saleDto);

    List<SaleDto> toListDto(List<Sale> sales);

    void updateModel(SaleDto saleDto, @MappingTarget Sale sale);
}
