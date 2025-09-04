package com.example.F5_Backend.service;

import com.example.F5_Backend.dto.UsersDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    public ResponseEntity<?> setUser(UsersDto usersDto);

    public ResponseEntity<?> login(UsersDto usersDto);

    public ResponseEntity<?> updateUser(UsersDto usersDto, MultipartFile image);
}
