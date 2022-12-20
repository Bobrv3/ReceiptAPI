package com.bobrov.receipt_api.dto.mapper;


import com.bobrov.receipt_api.dto.ProductDto;
import com.bobrov.receipt_api.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = SaleMapper.class)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toModel(ProductDto productDto);
    ProductDto toDto(Product product);
    void updateModel(ProductDto productDto, @MappingTarget Product product);
    List<ProductDto> toListDto(List<Product> byId);
}
