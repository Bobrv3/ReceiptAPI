package com.bobrov.checkApp.service.impl;

import com.bobrov.checkApp.dao.OrderRepository;
import com.bobrov.checkApp.model.Order;
import com.bobrov.checkApp.model.OrderItem;
import com.bobrov.checkApp.service.OrderService;
import com.bobrov.checkApp.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderImpl implements OrderService {
    private static final String FILE_PATH = "%s/receipt%s.txt";
    private static final String DIRECTORY_PATH = "./receipts";
    private final OrderRepository repository;
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
            %s   %s                $%s    $%s
            """;
    private static final String ITEM_OF_RECEIPT_WITH_SALE = """
            %s   %s                $%s    $%s
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
    public void init() throws IOException {
        Files.createDirectories(Paths.get(DIRECTORY_PATH));
    }

    @Override
    public Order findById(@Min(1) Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id));
    }

    @Override
    public List<Order> findAll(@Min(0) Integer offset, @Min(1) Integer limit) {
        return repository.findAll(PageRequest.of(offset, limit)).getContent();
    }

    // ToDO order validation(!null, description: length < 16)
    @Override
    public Order save(@NotNull Order order) {
        return repository.save(order);
    }

    @Override
    public Order update(@NotNull Order order) {
        findById(order.getId());

        return repository.save(order);
    }

    @Override
    public void delete(@Min(1) Long id) {
        findById(id);

        repository.deleteById(id);
    }

    @Override
    @SneakyThrows
    @Transactional
    public void makeReceipt(@Min(1) Long id) {
        //ToDO coorect receipt out info

        try (FileWriter writer = new FileWriter(String.format(FILE_PATH, DIRECTORY_PATH, id))) {
            /////// HEAD
            String receiptHead = String.format(HEAD_OF_RECEIPT, LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    , LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            /////// BODY
            StringBuilder receiptBuilder = new StringBuilder(receiptHead);

            Order order = findById(id);
            List<OrderItem> items = order.getItems();
            List<BigDecimal> itemsDiscounts = new ArrayList<>();
            for (OrderItem item : items) {
                switch (item.getProduct().getStatus()) {
                    // todo возможно сделать для скидок отдельную таблицу, где смогу задавать 1) кол-во, после которого применяется скидка 2) ее размер
                    case ON_SALE_IF_QUANTITY_GT_5 -> {
                        if (item.getQuantity() > 5) {
                            BigDecimal itemDiscount = makeBodyItemWithSale(receiptBuilder, item);
                            itemsDiscounts.add(itemDiscount);
                        }
                    }
                    default -> makeBodyItemWithoutSale(receiptBuilder, item);
                }
            }

            /////// FOOTER
            BigDecimal totalDiscountOnSalesItems = itemsDiscounts.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalPriceWithoutDiscount = items.stream()
                    .map(item -> item.getProduct().getPrice()
                            .multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalDiscountByCard = totalPriceWithoutDiscount
                    .multiply(calculateDiscountMultiplier(order.getDiscountCard().getDiscountSize()))
                    .setScale(2, RoundingMode.DOWN);

            BigDecimal totalDiscount = totalDiscountByCard.add(totalDiscountOnSalesItems);

            BigDecimal totalPriceWithDiscount = totalPriceWithoutDiscount.subtract(totalDiscount);
            receiptBuilder.append(String.format(FOOTER_OF_RECEIPT, totalPriceWithoutDiscount, totalDiscountByCard, totalDiscount, totalPriceWithDiscount));

            writer.write(receiptBuilder.toString());
        }
    }

    private BigDecimal makeBodyItemWithSale(StringBuilder receiptBuilder, OrderItem item) {
        BigDecimal totalPrice = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        BigDecimal discountOnSale = BigDecimal.valueOf(10);  // TODo вынести в сущность
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
