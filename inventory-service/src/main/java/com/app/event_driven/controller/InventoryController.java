package com.app.event_driven.controller;


import com.app.event_driven.model.Inventory;
import com.app.event_driven.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    @Autowired
    private final InventoryService inventoryService;

    @GetMapping("/{productId}")
    public Inventory getProduct(@PathVariable("productId") String productId){
        return inventoryService.getProductById(productId).orElse(null);
    }

    @PostMapping()
    public Inventory saveProduct(@RequestBody Inventory inventory){
        return inventoryService.addProduct(inventory);
    }

    @PutMapping()
    public Inventory updateProduct(@RequestBody Inventory inventory){
        return inventoryService.updateProduct(inventory);
    }

    @GetMapping()
    public List<Inventory> getProducts(){
        return inventoryService.getProducts();
    }

    @DeleteMapping("/{productId}")
    public void deleteProduct(@PathVariable("productId") String productId){
        inventoryService.deleteProduct(productId);
    }
}
