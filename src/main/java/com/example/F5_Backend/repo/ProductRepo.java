package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Integer> {

    Optional<Product> findByName(@NotNull @Size(max = 70) String name);
}
