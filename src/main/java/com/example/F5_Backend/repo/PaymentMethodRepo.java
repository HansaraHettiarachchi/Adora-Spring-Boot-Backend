package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepo extends JpaRepository<PaymentMethod, Integer> {
    PaymentMethod findByName(String name);
}

