package com.app.event_driven.order_service.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.app.event_driven.order_service.model.Order;
import com.app.event_driven.order_service.repository.OrderRepository;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AmazonSQS sqsClient;

    @Value("${aws.sqs.order.queueUrl}")
    private String orderQueueUrl;

    public Order createOrder(Order order){
        order.setStatus("PENDING");
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved:{}",savedOrder);
        String orderMessage = new Gson().toJson(savedOrder);
        SendMessageRequest sendMessageRequest = new SendMessageRequest(orderQueueUrl, orderMessage);
        sqsClient.sendMessage(sendMessageRequest);
        log.info("SQS msg is updated with msg:{}",sendMessageRequest);
        return savedOrder;
    }

    public List<Order> getOrders(){
        return orderRepository.findAll();
    }

    public Optional<Order> getOrderById(Long orderId){
        return orderRepository.findById(orderId);
    }

    public Order updateOrder(Order order){
        return orderRepository.save(order);
    }

    public boolean deleteOrder(Long orderId){
        if(getOrderById(orderId).isPresent()){
            orderRepository.delete(getOrderById(orderId).get());
            return true;
        }
        else{
            return false;
        }
    }
}
