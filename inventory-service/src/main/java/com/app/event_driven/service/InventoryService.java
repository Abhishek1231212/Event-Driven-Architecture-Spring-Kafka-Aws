package com.app.event_driven.service;


import com.app.event_driven.model.Inventory;
import com.app.event_driven.model.Order;
import com.app.event_driven.repository.InventoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;


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

    public String updateProductStock(Order order){
        Optional<Inventory> product = getProductById(order.getProductId());
        if(product.isPresent()){
            product.get().setQuantity(product.get().getQuantity() - order.getQuantity());
            inventoryRepository.save(product.get());
            return "SUCCESS";
        }
        else{
            return "PRODUCT_NOT_FOUND";
        }
    }

    @SqsListener("inventory-queue")
    public void updateInventory(String message){
        log.info("Updating Inventory of the inventory-queue message: {}", message);
        try {
            Order order = new ObjectMapper().convertValue(message, Order.class);
            String result = updateProductStock(order);
            log.info("Inventory updated the status: {}", result);
        }
        catch (Exception exception){
            log.error("Mapping failed due to error: {}", exception.getMessage());
        }
    }
}
