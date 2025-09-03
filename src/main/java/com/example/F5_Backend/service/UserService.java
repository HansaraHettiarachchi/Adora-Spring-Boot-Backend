package com.example.F5_Backend.service;

import com.example.F5_Backend.dto.UsersDto;
import org.springframework.http.ResponseEntity;

public interface UserService {
    public ResponseEntity<?> setUser(UsersDto usersDto);

    public ResponseEntity<?> login(UsersDto usersDto);
}
