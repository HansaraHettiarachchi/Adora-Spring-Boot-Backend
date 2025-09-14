package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderStatusRepo extends JpaRepository<OrderStatus, Integer> {
}
