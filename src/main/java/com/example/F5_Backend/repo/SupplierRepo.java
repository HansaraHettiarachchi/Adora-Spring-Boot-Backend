package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepo extends JpaRepository<Supplier, Integer> {
}

