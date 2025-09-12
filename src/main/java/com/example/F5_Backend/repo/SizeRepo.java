package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SizeRepo extends JpaRepository<Size, Integer> {
}