package com.app.event_driven.service;


import com.app.event_driven.model.Inventory;
import com.app.event_driven.model.Order;
import com.app.event_driven.repository.InventoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final JsonMapper jsonMapper = new JsonMapper();

    public Inventory addProduct(Inventory inventory){
        return inventoryRepository.save(inventory);
    }

    public Inventory updateProduct(Inventory inventory){
        return inventoryRepository.save(inventory);
    }

    public List<Inventory> getProducts(){
        return inventoryRepository.findAll();
    }

    public Optional<Inventory> getProductById(String productId){
        return inventoryRepository.findById(productId);
    }

    public void deleteProduct(String productId){
        inventoryRepository.deleteById(productId);
    }


    @SqsListener("inventory-queue")
    public void getMessages(String message){
        log.info("InventoryService | getMessages() | received message: {}", message);
        String actualMessage = "";
        try {
            JsonNode rootNode = objectMapper.readTree(message);
            String messageContent = rootNode.get("Message").asText();
            actualMessage = cleanJsonMessage(messageContent);
        } catch (Exception e) {
            log.error("SQS message failed due to: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        log.info("InventoryService | getMessages() | Processing message");
        updateInventory(actualMessage);
    }

    private void updateInventory(String message){

        log.info("InventoryService | updateInventory() | updating Inventory of the inventory-queue message: {}", message);
        try {
            JsonNode orderNode = objectMapper.readTree(message);
            String orderId = orderNode.get("orderId").asText();
            String productId = orderNode.get("productId").asText();
            int quantity = orderNode.get("quantity").asInt();
            String result = updateProductStock(orderId, productId, quantity);
            log.info("InventoryService | updateInventory() | inventory updated the status: {}", result);
        }
        catch (Exception exception){
            log.error("InventoryService | updateInventory() | mapping failed due to error: {}", exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    public String updateProductStock(String orderId, String productId, int quantity){
        log.info(
                "InventoryService | updateProductStock() | " +
                        "updating inventory stock for productId: {} by for the orderId: {} with quantity: {}",
                productId, orderId, quantity);
        Optional<Inventory> product = getProductById(productId);
        if(product.isPresent()){
            product.get().setQuantity(product.get().getQuantity() - quantity);
            inventoryRepository.save(product.get());
            return "SUCCESS";
        }
        else{
            return "PRODUCT_NOT_FOUND";
        }
    }

    private String cleanJsonMessage(String message) {
        return message.replace("message: ", "");
    }
}
