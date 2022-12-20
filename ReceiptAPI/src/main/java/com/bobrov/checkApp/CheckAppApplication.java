package com.bobrov.checkApp;

import com.bobrov.checkApp.dto.DiscountCardDto;
import com.bobrov.checkApp.dto.OrderDto;
import com.bobrov.checkApp.dto.OrderItemDto;
import com.bobrov.checkApp.dto.ProductDto;
import com.bobrov.checkApp.model.Order;
import com.bobrov.checkApp.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class CheckAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(@Autowired OrderService orderService) {
        return args -> {
            DiscountCardDto discountCard = null;
            List<OrderItemDto> items = new ArrayList<>();

            for (String arg : args) {
                String cardId = null;
                String productId = null;
                String quantity = null;

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

            Order order = orderService.save(OrderDto.builder()
                    .discountCard(discountCard)
                    .items(items)
                    .build());

            orderService.makeReceipt(order.getId());
        };
    }

}
