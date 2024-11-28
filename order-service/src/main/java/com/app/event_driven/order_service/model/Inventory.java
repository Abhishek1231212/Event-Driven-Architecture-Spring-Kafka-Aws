package com.app.event_driven.order_service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {

    private String productId;
    private int quantity;
    private String productName;
    private long price;
}
