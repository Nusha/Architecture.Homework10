package com.example.cafeapp.model;

import java.util.List;

// Класс агрегатор
public class Order {
    private List<OrderItem> items;
    private String id;

    public double calculateTotalPrice() {
        return items.stream().mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity()).sum();
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}

