package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Batch;
import com.example.F5_Backend.entities.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BatchRepo extends JpaRepository<Batch, Integer> {
    Batch findByProductIdAndSizeIdAndCostAndPrice(Integer productId, Integer sizeId, @NotNull Double cost, @NotNull Double price);

    Batch findByCode(@Size(max = 30) @NotNull String code);

    List<Batch> findByProductId(Integer productId);

    @Query("SELECT SUM(b.qty) FROM Batch b WHERE b.product.id = :productId")
    Integer findTotalQtyOfProductId(Integer productId);

    Batch findDistinctFirstById(Integer id);

    Batch findDistinctFirstByProduct(Product product);
}