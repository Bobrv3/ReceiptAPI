package com.bobrov.checkApp.service.impl;

import com.bobrov.checkApp.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@SqlGroup({
        @Sql("/schema.sql"),
        @Sql("/test_data.sql")

})
class OrderImplTest {
//    @Mock
//    OrderRepository repository;
//
//    @InjectMocks
//    OrderImpl service;
    @Autowired
    private OrderService service;

    @Test
    void findById() {
    }

    @Test
    void findAll() {
    }

    @Test
    void save() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void makeReceipt() {
        assertDoesNotThrow(() -> service.makeReceipt(1L));
    }
}