package com.app.event_driven.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Order {
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
