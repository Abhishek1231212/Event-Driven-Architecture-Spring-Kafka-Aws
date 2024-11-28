package com.app.event_driven.service;


import com.app.event_driven.model.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShippingService {

    @SqsListener("shipping-queue")
    public String processShipping(String message){
        Order order = new ObjectMapper().convertValue(message, Order.class);
        log.info("Shipping process is started for the orderId: {} to address: {}", order.getOrderId(), order.getAddress());
        return "Shipping process is started for the orderId: "+ order.getOrderId() + "to address: " + order.getAddress();
    }
}
