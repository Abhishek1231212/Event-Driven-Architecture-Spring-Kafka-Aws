package com.app.event_driven.payment_service.controller;


import com.app.event_driven.payment_service.model.Order;
import com.app.event_driven.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> processPayment(@RequestBody Order order){
        String message = paymentService.processPayment(order);
        return ResponseEntity.ok(message);
    }

//    @GetMapping
//    public ResponseEntity<String> processPayment(){
//        paymentListener.pollSqsMessages();
//        return ResponseEntity.ok("Payment processed");
//    }

//    @PostMapping("/orderId/{id}")
//    public ResponseEntity<String> processPayment(@PathVariable Long id){
//        boolean response = paymentService.processPayment(id);
//        if(response)
//            return ResponseEntity.ok("Payment processed for orderid " + id);
//        else
//            return ResponseEntity.badRequest().body("Error");
//    }

}
