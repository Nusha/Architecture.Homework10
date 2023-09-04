package com.example.cafeapp.model;

import java.util.List;

public interface OrderRepository {
    Order save(Order order);

    Order findById(String id);

    List<Order> findAll(int page, int size);

    void deleteById(String id);
}
