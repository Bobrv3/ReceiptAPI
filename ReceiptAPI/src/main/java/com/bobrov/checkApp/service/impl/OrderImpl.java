package com.bobrov.checkApp.service.impl;

import com.bobrov.checkApp.dao.DiscountCardRepository;
import com.bobrov.checkApp.dao.OrderRepository;
import com.bobrov.checkApp.dao.ProductRepository;
import com.bobrov.checkApp.dto.OrderDto;
import com.bobrov.checkApp.dto.mapper.OrderItemMapper;
import com.bobrov.checkApp.model.DiscountCard;
import com.bobrov.checkApp.model.Order;
import com.bobrov.checkApp.model.OrderItem;
import com.bobrov.checkApp.model.Product;
import com.bobrov.checkApp.model.Sale;
import com.bobrov.checkApp.service.OrderService;
import com.bobrov.checkApp.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class OrderImpl implements OrderService {

    private final Path path = Paths.get("receipts");
    private static final String FILE_PATH = "%s/%s.txt";
    private static final String DIRECTORY_PATH = "./receipts";
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final DiscountCardRepository discountCardRepository;
    private static final String HEAD_OF_RECEIPT = """
                         CASH RECEIPT
                        SUPERMARKET-123
                    12, MILKYWAY Galaxy/ Earth
                        Tel: 123-456-7890
                  
            CASHIER: №1520       DATE: %s
                                 TIME: %s
            -------------------------------------
            QTY DESCRIPTION         PRICE   TOTAL
            """;
    private static final String ITEM_OF_RECEIPT_WITHOUT_SALE = """
            %s\t%s\t\t$%s\t$%s
            """;
    private static final String ITEM_OF_RECEIPT_WITH_SALE = """
            %s\t%s\t\t$%s\t$%s
                                 discount = $%s
            """;
    private static final String FOOTER_OF_RECEIPT = """
            -------------------------------------
            Total without disc              $%s
            =====================================
            CARD DISCOUNT                   $%s
            TOTAL DISCOUNT                  $%s
            =====================================
            TOTAL                           $%s
            """;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(DIRECTORY_PATH));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public Order findById(@Min(1) Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public Page<Order> findAll(@Min(0) Integer offset, @Min(1) Integer limit) {
        return orderRepository.findAll(PageRequest.of(offset, limit));
    }

    @Override
    @Transactional
    public Order save(@Valid OrderDto orderDto) {
        Order newOrder = new Order();

        DiscountCard discountCard = discountCardRepository.findById(orderDto.getDiscountCard().getId())
                .orElseThrow(() -> new NotFoundException(orderDto.getDiscountCard().getId()));
        newOrder.setDiscountCard(discountCard);

        orderDto.getItems().stream()
                .forEach(orderItem -> {
                    Product product = productRepository.findById(orderItem.getProduct().getId())
                            .orElseThrow(() -> new NotFoundException(orderItem.getProduct().getId()));

                    OrderItem item = new OrderItem();
                    item.setProduct(product);
                    item.setQuantity(orderItem.getQuantity());

                    newOrder.addItem(item);
                });

        return orderRepository.save(newOrder);

    }

    @Override
    @Transactional
    public Order update(@Min(1) Long id, @Valid OrderDto orderDto) {
        Order order = findById(id);

        order.getItems().clear();
        orderRepository.flush();

        if (orderDto.getStatus() != null) {
            order.setStatus(orderDto.getStatus());
        }

        DiscountCard discountCard = discountCardRepository.findById(orderDto.getDiscountCard().getId())
                .orElseThrow(() -> new NotFoundException(orderDto.getDiscountCard().getId()));
        order.setDiscountCard(discountCard);

        orderDto.getItems().stream()
                .forEach(orderItem -> {
                    Product product = productRepository.findById(orderItem.getProduct().getId())
                            .orElseThrow(() -> new NotFoundException(orderItem.getProduct().getId()));

                    OrderItem item = OrderItemMapper.INSTANCE.toItem(orderItem);
                    order.addItem(item);
                    item.setProduct(product);
                    item.setQuantity(orderItem.getQuantity());
                });

        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteById(@Min(1) Long id) {
        Order order = findById(id);

        order.setStatus(Order.OrderStatus.DELETED);
    }

    @Override
    @Transactional
    public void makeReceipt(@Min(1) Long id) {
        Order order = findById(id);

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(String.format(FILE_PATH, DIRECTORY_PATH, id)), StandardCharsets.UTF_8))) {

            String receiptHead = String.format(HEAD_OF_RECEIPT, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    , LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            StringBuilder receiptBuilder = new StringBuilder(receiptHead);

            List<OrderItem> items = order.getItems();
            List<BigDecimal> itemsDiscounts = new ArrayList<>();
            for (OrderItem item : items) {
                Sale sale = item.getProduct().getSale();
                if (sale != null && item.getQuantity() > sale.getFromQuantity()) {
                    BigDecimal itemDiscount = makeBodyItemWithSale(receiptBuilder, item);
                    itemsDiscounts.add(itemDiscount);
                } else {
                    makeBodyItemWithoutSale(receiptBuilder, item);
                }
            }

            BigDecimal totalDiscountOnSalesItems = itemsDiscounts.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalPriceWithoutDiscount = items.stream()
                    .map(item -> item.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            DiscountCard discountCard = order.getDiscountCard();
            BigDecimal totalDiscountByCard = BigDecimal.ZERO;
            if (discountCard != null) {
                totalDiscountByCard = totalPriceWithoutDiscount
                        .multiply(calculateDiscountMultiplier(discountCard.getDiscountSize()))
                        .setScale(2, RoundingMode.DOWN);
            }

            BigDecimal totalDiscount = totalDiscountByCard.add(totalDiscountOnSalesItems);

            BigDecimal totalPriceWithDiscount = totalPriceWithoutDiscount.subtract(totalDiscount);
            receiptBuilder.append(String.format(FOOTER_OF_RECEIPT, totalPriceWithoutDiscount, totalDiscountByCard, totalDiscount, totalPriceWithDiscount));

            writer.write(receiptBuilder.toString());
            System.out.println(receiptBuilder);
        } catch (IOException e) {
            throw new RuntimeException("Problems with file for saving receipt");
        }
    }

    @Override
    public Resource load(@Min(1) Long id) {
        Path file = path.resolve(String.format("%s.txt", id));

        try {
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error:" + e.getMessage());
        }
    }

    private BigDecimal makeBodyItemWithSale(StringBuilder receiptBuilder, OrderItem item) {
        BigDecimal totalPrice = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        BigDecimal discountOnSale = item.getProduct().getSale().getDiscountSize();
        BigDecimal discountOnItem = totalPrice.multiply(calculateDiscountMultiplier(discountOnSale))
                .setScale(2, RoundingMode.DOWN);

        receiptBuilder.append(String.format(ITEM_OF_RECEIPT_WITH_SALE,
                item.getQuantity(),
                item.getProduct().getDescription(),
                item.getProduct().getPrice(),
                totalPrice,
                discountOnItem
        ));

        return discountOnItem;
    }

    private BigDecimal calculateDiscountMultiplier(BigDecimal discountOnSale) {
        return discountOnSale.divide(BigDecimal.valueOf(100));
    }

    private void makeBodyItemWithoutSale(StringBuilder receiptBuilder, OrderItem item) {
        receiptBuilder.append(String.format(ITEM_OF_RECEIPT_WITHOUT_SALE,
                item.getQuantity(),
                item.getProduct().getDescription(),
                item.getProduct().getPrice(),
                item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
        ));
    }
}
