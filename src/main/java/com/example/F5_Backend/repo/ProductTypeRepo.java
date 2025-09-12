package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductTypeRepo extends JpaRepository<ProductType, Integer> {
}