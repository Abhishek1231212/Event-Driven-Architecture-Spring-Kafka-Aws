package com.app.event_driven.payment_service.controller;


import com.app.event_driven.payment_service.model.Order;
import com.app.event_driven.payment_service.model.Payment;
import com.app.event_driven.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Autowired
    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<Payment>> getPayments(){
        return ResponseEntity.ok(paymentService.getPayments());
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable("transactionId") String transactionId){
        return ResponseEntity.ok(paymentService.getPaymentById(transactionId).orElseGet(null));
    }

    @PostMapping
    public ResponseEntity<String> processPayment(@RequestBody Order order){
        String message = paymentService.processPayment(order);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<String> deletePaymentById(@PathVariable("transactionId") String transactionId){
        return ResponseEntity.ok(paymentService.deletePayment(transactionId));
    }

}
