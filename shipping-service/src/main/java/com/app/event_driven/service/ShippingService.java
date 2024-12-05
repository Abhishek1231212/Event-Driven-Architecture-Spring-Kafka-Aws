package com.app.event_driven.service;


import com.app.event_driven.model.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShippingService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SqsListener("shipping-queue")
    public void getMessages(String message) throws Exception {
        log.info("ShippingService | getMessages() | message received from shipping-queue. Message: {}", message);
        String actualMessage = "";
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            String messageContent = rootNode.get("Message").asText();
            actualMessage = cleanJsonMessage(messageContent);
        } catch (Exception e) {
            log.error("SQS message failed due to: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        log.info("Processing order");
        processShipping(actualMessage);
    }

    public String processShipping(String message) {
        try {
            JsonNode orderNode = objectMapper.readTree(message);
            String orderId = orderNode.get("orderId").asText();
            String address = orderNode.get("address").asText();
            log.info("ShippingService | getMessages() | shipping process is started for the orderId: {} to address: {}", orderId, address);
            return "Shipping process is started for the orderId: " + orderId + "to address: " + address;
        }
        catch (Exception exception){
            log.error("ShippingService | getMessages() | error in retrieving data due to: {}", exception.getMessage());
            return "Error";
        }

    }

    private String cleanJsonMessage(String message) {
        return message.replace("message: ", "");
    }
}
