package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Batch;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BatchRepo extends JpaRepository<Batch, Integer> {
    Batch findByProductIdAndSizeIdAndCostAndPrice(Integer productId, Integer sizeId, @NotNull Double cost, @NotNull Double price);

    Batch findByCode(@Size(max = 30) @NotNull String code);
}