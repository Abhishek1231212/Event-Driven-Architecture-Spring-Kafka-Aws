package com.app.event_driven.payment_service.controller;


import com.app.event_driven.payment_service.service.PaymentListener;
import com.app.event_driven.payment_service.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired
    private PaymentListener paymentListener;

    @Autowired
    private PaymentService paymentService;

    @GetMapping
    public ResponseEntity<String> processPayment(){
        paymentListener.pollSqsMessages();
        return ResponseEntity.ok("Payment processed");
    }

    @PostMapping("/orderId/{id}")
    public ResponseEntity<String> processPayment(@PathVariable Long id){
        boolean response = paymentService.processPayment(id);
        if(response)
            return ResponseEntity.ok("Payment processed for orderid " + id);
        else
            return ResponseEntity.badRequest().body("Error");
    }

}
