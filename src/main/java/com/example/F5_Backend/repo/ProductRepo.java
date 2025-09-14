package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product, Integer> {

    Optional<Product> findByName(@NotNull @Size(max = 70) String name);

    Page<Product> findByIsActive(Boolean isActive, Pageable pageable);

    List<Product> findByIsActive(Boolean isActive);

    @Query("SELECT p FROM Product p " +
           "INNER JOIN p.category c " +
           "INNER JOIN p.motherPlantType m " +
           "WHERE p.isActive = :isActive AND (" +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%'))" +
           ")")
    Page<Product> searchActiveProducts(Boolean isActive, String search, Pageable pageable);

    @Query("SELECT p FROM Product p " +
           "INNER JOIN p.category c " +
           "INNER JOIN p.motherPlantType m " +
           "WHERE p.isActive = :isActive AND (" +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%'))" +
           ")")
    List<Product> searchActiveProducts(Boolean isActive, String search);

}
