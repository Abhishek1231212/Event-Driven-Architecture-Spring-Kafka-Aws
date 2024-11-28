package com.app.event_driven.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private String orderId;
    private String transactionId;
    private String productId;
    private String created;
    private String modified;
    private String productName;
    private String status;
    private String address;
    private long price;
    private int quantity;
}
