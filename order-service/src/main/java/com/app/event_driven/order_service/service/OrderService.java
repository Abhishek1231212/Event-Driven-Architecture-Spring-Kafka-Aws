package com.app.event_driven.order_service.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.app.event_driven.order_service.model.Inventory;
import com.app.event_driven.order_service.model.Order;
import com.app.event_driven.order_service.model.Payment;
import com.app.event_driven.order_service.repository.OrderRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AmazonSQS sqsClient;

    @Autowired
    private WebClient webClient;

    @Value("${aws.sqs.inventory.queueUrl}")
    private String inventoryQueueUrl;

    @Value("${aws.sqs.shipping.queueUrl}")
    private String shippingQueueUrl;

    @Value("${aws.sqs.notification.queueUrl}")
    private String notificationQueueUrl;

//    public Order createOrder(Order order){
//        order.setStatus("PENDING");
//        Order savedOrder = orderRepository.save(order);
//        log.info("Order saved:{}",savedOrder);
//        String orderMessage = new Gson().toJson(savedOrder);
//        SendMessageRequest sendMessageRequest = new SendMessageRequest(inventoryQueueUrl, orderMessage);
//        sqsClient.sendMessage(sendMessageRequest);
//        log.info("SQS msg is updated with msg:{}",sendMessageRequest);
//        return savedOrder;
//    }
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


    public Order createOrder(Order order){
        Order savedOrder = orderRepository.save(order);
        log.info("Order initiated:{}",savedOrder);
        log.info("checking inventory");
        Inventory inventory =
                webClient.get().uri("http://localhost:8082/api/inventory/" + order.getProductId())
                        .retrieve().bodyToMono(Inventory.class).block();
        log.info("Inventory details: {}", inventory);
        if(inventory != null && inventory.getQuantity()<1){
            order.setPrice(inventory.getPrice());
            order.setProductName(inventory.getProductName());
            log.info("Payment Initiated for the orderId: {}", order.getOrderId());
//            Payment payment =
//                    webClient.get().uri("http://localhost:8082/api/payment/" + order.getProductId())
//                            .retrieve().bodyToMono(Inventory.class).block();
        }
        return savedOrder;
    }

}
