package com.app.event_driven.order_service.service;


import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.app.event_driven.order_service.model.Inventory;
import com.app.event_driven.order_service.model.Order;
import com.app.event_driven.order_service.repository.OrderRepository;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final String SUCCESS = "SUCCESS";

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AmazonSQS sqsClient;

    @Autowired
    private AmazonSNS snsClient;

    @Autowired
    private WebClient webClient;

    @Value("${aws.sns.order-topic.arn}")
    private String snsTopicArn;

    @Value("${aws.sqs.inventory.queueUrl}")
    private String inventoryQueueUrl;

    @Value("${aws.sqs.shipping.queueUrl}")
    private String shippingQueueUrl;

    @Value("${aws.sqs.notification.queueUrl}")
    private String notificationQueueUrl;

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
        order.setCreated(LocalDate.now().toString());
        log.info("Order initiated:{}",order);
        log.info("checking inventory");
        try {
            Inventory inventory = getInventoryData(order);
            log.info("Inventory details: {}", inventory);
            if (inventory != null && inventory.getQuantity() > 0 && inventory.getQuantity() > order.getQuantity()) {
                log.info("Payment Initiated for the orderId: {}", order.getOrderId());
                order.setPrice(inventory.getPrice());
                order.setProductName(inventory.getProductName());
                order.setModified(LocalDate.now().toString());
                String transactionId = processPayment(order);
                order.setTransactionId(transactionId);
                order.setStatus( (!ObjectUtils.isEmpty(transactionId)) ? "SUCCESSFULLY_CREATED" : "PAYMENT_FAILED" );
                Order savedOrder = orderRepository.save(order);
                updateInventory(savedOrder);
                processShipping(savedOrder);
                return savedOrder;
            } else {
                order.setStatus("INVENTORY_FAILED");
                return order;
            }
        }
        catch(Exception exception){
            log.error("Exception occurred: {}", exception.getMessage());
            return null;
        }
    }

    private String processPayment(Order order) {
        return webClient.post().uri("http://localhost:8081/api/payment")
                        .bodyValue(order)
                        .retrieve().bodyToMono(String.class).block();
    }

    private Inventory getInventoryData(Order order) {
        return webClient.get().uri("http://localhost:8082/api/inventory/" + order.getProductId())
                        .retrieve().bodyToMono(Inventory.class).block();
    }

    public String updateInventory(Order order){
        log.info("Started updating inventory for orderId: {}, productId: {}", order.getOrderId(), order.getProductId());
        sendOrderMessage(new Gson().toJson(order), "inventoryService");
        return SUCCESS;
    }

    public String processShipping(Order order){
        log.info("Started processing shipping for orderId: {}, address: {}", order.getOrderId(), order.getAddress());
        sendOrderMessage(new Gson().toJson(order), "shippingService");
        return SUCCESS;
    }

    public String sendNotification(Order order){
        log.info("Started sending notification for order: {}", order);
        sendOrderMessage(new Gson().toJson(order), "notificationService");
        return SUCCESS;
    }

    public void sendOrderMessage(String message, String serviceName) {

        MessageAttributeValue messageAttributeValue = new MessageAttributeValue();
        messageAttributeValue.setDataType("String");
        messageAttributeValue.setStringValue(serviceName);

        Map<String, MessageAttributeValue> messageAttributeValueMap = new HashMap<>();
        messageAttributeValueMap.put("serviceName", messageAttributeValue);


        PublishRequest publishRequest = new PublishRequest();
        publishRequest.setTopicArn(snsTopicArn);
        publishRequest.setMessage("message: " + message);
        publishRequest.setMessageAttributes(messageAttributeValueMap);

        PublishResult response = snsClient.publish(publishRequest);
        log.info("Message ID: " + response.getMessageId());
    }
}
