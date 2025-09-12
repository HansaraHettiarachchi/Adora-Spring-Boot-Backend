package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Grn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

public interface GrnRepo extends JpaRepository<Grn, Integer> {
}