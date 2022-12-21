package com.bobrov.receipt_api.config;

import com.bobrov.receipt_api.dto.DiscountCardDto;
import com.bobrov.receipt_api.dto.OrderDto;
import com.bobrov.receipt_api.dto.OrderItemDto;
import com.bobrov.receipt_api.dto.ProductDto;
import com.bobrov.receipt_api.model.Order;
import com.bobrov.receipt_api.service.OrderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CommandLineRunnerConfig {
    @Bean
    public CommandLineRunner commandLineRunner(OrderService orderService) {
        return args -> {
            DiscountCardDto discountCard = null;
            List<OrderItemDto> items = new ArrayList<>();

            for (String arg : args) {
                String cardId;
                String productId;
                String quantity;

                if ("card".equals(arg.split("-")[0])) {
                    cardId = arg.split("-")[1];
                    discountCard = DiscountCardDto.builder()
                            .id(Long.valueOf(cardId))
                            .build();
                } else {
                    productId = arg.split("-")[0];
                    quantity = arg.split("-")[1];

                    ProductDto product = ProductDto.builder()
                            .id(Long.valueOf(productId))
                            .build();

                    items.add(OrderItemDto.builder()
                            .product(product)
                            .quantity(Integer.valueOf(quantity))
                            .build());
                }
            }

            if (!items.isEmpty()) {
                Order order = orderService.save(OrderDto.builder()
                        .discountCard(discountCard)
                        .items(items)
                        .build());

                orderService.makeReceipt(order.getId());
            }
        };
    }
}
