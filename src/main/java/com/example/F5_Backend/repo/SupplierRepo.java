package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface SupplierRepo extends JpaRepository<Supplier, Integer> {
}