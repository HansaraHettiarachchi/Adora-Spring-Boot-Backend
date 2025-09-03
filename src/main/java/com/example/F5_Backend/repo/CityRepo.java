package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepo extends JpaRepository<City, Integer> {
}
