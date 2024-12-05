package com.app.event_driven.payment_service.service;



import com.app.event_driven.payment_service.model.Order;
import com.app.event_driven.payment_service.model.Payment;
import com.app.event_driven.payment_service.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public List<Payment> getPayments(){
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(String transactionId){
        return paymentRepository.findById(transactionId);
    }

    public String deletePayment(String transactionId){
        Optional<Payment> payment = paymentRepository.findById(transactionId);
        if(payment.isPresent()) {
            paymentRepository.deleteById(transactionId);
            return "DELETED";
        }
        else {
            return "NO_PAYMENT_FOUND";
        }
    }

    public String processPayment(Order order){
        log.info("PaymentService | processPayment() | processing payment for the orderId: {}", order.getOrderId());
        String transactionId = UUID.randomUUID().toString();
        Payment payment = new Payment();
        payment.setTransactionId(transactionId);
        payment.setPrice(order.getPrice());
        payment.setCreated(LocalDate.now());
        payment.setModified(LocalDate.now());
        payment.setOrderId(order.getOrderId());
        log.info(
                "PaymentService | processPayment() | payment processed for the orderId: {} with transactionId: {}",
                order.getOrderId(), payment.getTransactionId());
        paymentRepository.save(payment);
        return payment.getTransactionId();
    }
}
