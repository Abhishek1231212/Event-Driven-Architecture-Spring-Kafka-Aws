package com.app.event_driven.order_service.controller;


import com.app.event_driven.order_service.model.Order;
import com.app.event_driven.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {


    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order){
        Order createdOrder = orderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders(){
        return ResponseEntity.ok(orderService.getOrders());
    }

    @PutMapping
    public ResponseEntity<Order> updateOrder(@RequestBody Order order){
        return ResponseEntity.ok(orderService.updateOrder(order));
    }

    @GetMapping("/orderId/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id){
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(orderService.getOrderById(id).get());
        }
        else{
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/orderId/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Long id){
        boolean response = orderService.deleteOrder(id);
        if(response){
            return ResponseEntity.ok("Delete successfully");
        }
        else{
            return ResponseEntity.badRequest().body("No order");
        }
    }

}
