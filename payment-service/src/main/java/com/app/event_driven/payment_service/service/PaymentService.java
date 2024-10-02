package com.app.event_driven.payment_service.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.app.event_driven.payment_service.model.Order;
import com.app.event_driven.payment_service.repository.OrderRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AmazonSQS amazonSQS;

    @Value("${aws.sqs.payment.queueUrl}")
    private String paymentQueueUrl;

    public void processPayment(String orderJson){
        Order order = new Gson().fromJson(orderJson, Order.class);
        order.setStatus("PAID");
        orderRepository.save(order);
        log.info("Payment updated:{}",order);
        SendMessageRequest sendMessageRequest = new SendMessageRequest(paymentQueueUrl, new Gson().toJson(order));
        amazonSQS.sendMessage(sendMessageRequest);
        log.info("SQS msg is updated with msg:{}",sendMessageRequest);
    }

    public boolean processPayment(Long id){
        Optional<Order> order = orderRepository.findById(id);
        if(order.isPresent()) {
            order.get().setStatus("PAID");
            orderRepository.save(order.get());
            log.info("Payment updated:{}", order);
            SendMessageRequest sendMessageRequest = new SendMessageRequest(paymentQueueUrl, new Gson().toJson(order.get()));
            amazonSQS.sendMessage(sendMessageRequest);
            log.info("SQS msg is updated with msg:{}", sendMessageRequest);
            return true;
        }
        else{
            log.error("There is an error with orderID");
            return false;
        }
    }
}
