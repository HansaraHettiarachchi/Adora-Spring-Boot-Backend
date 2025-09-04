package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Users;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepo extends JpaRepository<Users, Integer> {
    boolean existsByEmail(String email);

    boolean existsByMobile(String mobile);

    boolean existsByNic(String nic);

    Optional<Users> findByEmail(@NotNull @Size(max = 100) @Email String email);

    Users findByMobile(@NotNull @Size(max = 20) String mobile);

    Users findByNic(@Size(max = 20) @NotNull String nic);
}
