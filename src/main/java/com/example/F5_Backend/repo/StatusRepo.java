package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Status;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRepo extends JpaRepository<Status, Integer> {
}
