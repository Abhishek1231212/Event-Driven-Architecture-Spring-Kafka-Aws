package com.app.event_driven.order_service.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.app.event_driven.order_service.model.Inventory;
import com.app.event_driven.order_service.model.Order;
import com.app.event_driven.order_service.model.Payment;
import com.app.event_driven.order_service.repository.OrderRepository;
import io.netty.util.internal.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public Optional<Order> getOrderById(String orderId){
        return orderRepository.findById(orderId);
    }

    public Order updateOrder(Order order){
        return orderRepository.save(order);
    }

    public boolean deleteOrder(String orderId){
        if(getOrderById(orderId).isPresent()){
            orderRepository.delete(getOrderById(orderId).get());
            return true;
        }
        else{
            return false;
        }
    }


    public Order createOrder(Order order){
        order.setOrderId(UUID.randomUUID().toString());
        order.setCreated(LocalDate.now());
        log.info("Order initiated:{}",order);
        log.info("checking inventory");
        try {
            Inventory inventory =
                    webClient.get().uri("http://localhost:8082/api/inventory/" + order.getProductId())
                            .retrieve().bodyToMono(Inventory.class).block();
            log.info("Inventory details: {}", inventory);
            if (inventory != null && inventory.getQuantity() > 0) {
                order.setPrice(inventory.getPrice());
                order.setProductName(inventory.getProductName());
                log.info("Payment Initiated for the orderId: {}", order.getOrderId());
            String transactionId =
                    webClient.post().uri("http://localhost:8081/api/payment")
                            .bodyValue(order)
                            .retrieve().bodyToMono(String.class).block();
                order.setModified(LocalDate.now());
                order.setStatus((!ObjectUtils.isEmpty(transactionId))?"CREATED":"PAYMENT_FAILED");
                orderRepository.save(order);
                return order;
            } else {
                order.setStatus("INVENTORY_FAILED");
                return order;
            }
        }
        catch(Exception exception){
            log.error("Exception occured: {}", exception.getMessage());
            return null;
        }
    }

}
