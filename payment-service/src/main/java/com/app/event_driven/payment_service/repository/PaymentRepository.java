package com.app.event_driven.payment_service.repository;


import com.app.event_driven.payment_service.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
