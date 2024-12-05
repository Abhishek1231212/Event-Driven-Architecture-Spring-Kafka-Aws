package com.app.event_driven.order_service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    @Id
    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("transactionId")
    private String transactionId;

    @JsonProperty("productId")
    private String productId;

    @JsonProperty("created")
    private String created;

    @JsonProperty("modified")
    private String modified;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("address")
    private String address;

    @JsonProperty("price")
    private long price;

    @JsonProperty("quantity")
    private int quantity;

}
