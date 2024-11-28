package com.app.event_driven.service;


import com.app.event_driven.model.Inventory;
import com.app.event_driven.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
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
}
