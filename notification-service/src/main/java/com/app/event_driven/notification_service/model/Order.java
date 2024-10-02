package com.app.event_driven.notification_service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    private Long id;
    private String productName;
    private int quantity;
    private double price;
    private String status;
}