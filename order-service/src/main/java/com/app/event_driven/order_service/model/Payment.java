package com.app.event_driven.order_service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    private String transactionId;
    private long price;
    private LocalDate created;
    private LocalDate modified;
    private String orderId;
}
