package com.app.event_driven.inventory_service.repository;


import com.app.event_driven.inventory_service.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
}
