package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Gender;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenderRepo extends JpaRepository<Gender, Integer> {
}
