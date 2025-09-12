package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.GrnItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface GrnItemRepo extends JpaRepository<GrnItem, Integer> {
}