package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepo extends JpaRepository<UserRole, Integer> {
}
