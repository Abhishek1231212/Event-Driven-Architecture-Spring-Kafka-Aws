package com.app.event_driven.payment_service.repository;


import com.app.event_driven.payment_service.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
