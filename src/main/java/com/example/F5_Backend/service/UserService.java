package com.example.F5_Backend.service;

import com.example.F5_Backend.dto.UsersDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    ResponseEntity<?> setUser(UsersDto usersDto);

    ResponseEntity<?> updateUser(UsersDto usersDto, MultipartFile image);

    ResponseEntity<?> getAllCities(Integer id);

    ResponseEntity<?> getAllGender(Integer id);
}
