package com.app.event_driven.payment_service.service;



import com.app.event_driven.payment_service.model.Order;
import com.app.event_driven.payment_service.model.Payment;
import com.app.event_driven.payment_service.repository.PaymentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    public String processPayment(Order order){
        String transactionId = UUID.randomUUID().toString();
        Payment payment = new Payment();
        payment.setTransactionId(transactionId);
        payment.setPrice(order.getPrice());
        payment.setCreated(LocalDate.now());
        payment.setModified(LocalDate.now());
        payment.setOrderId(order.getOrderId());
        paymentRepository.save(payment);
        return payment.getTransactionId();
    }
}
