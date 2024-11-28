package com.app.event_driven.inventory_service.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private Long id;
    private String productName;
    private int quantity;
    private double price;
    private String status;
}
