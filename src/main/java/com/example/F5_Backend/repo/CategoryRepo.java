package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Integer> {
}