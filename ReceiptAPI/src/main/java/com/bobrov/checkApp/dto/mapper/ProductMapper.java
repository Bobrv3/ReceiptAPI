package com.bobrov.checkApp.dto.mapper;


import com.bobrov.checkApp.dto.ProductDto;
import com.bobrov.checkApp.model.Product;
import com.bobrov.checkApp.model.SaleStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(imports = SaleStatus.class)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "status", defaultExpression = "java(Product.ProductStatus.ENABLE)")
    @Mapping(target = "saleStatus", defaultExpression = "java(SaleStatus.NOT_ON_SALE)")
    Product toModel(ProductDto productDto);
    ProductDto toDto(Product product);
    void updateModel(ProductDto productDto, @MappingTarget Product product);
    List<ProductDto> toListDto(List<Product> byId);
}
