package com.example.F5_Backend.service.impl;

import com.example.F5_Backend.dto.UsersDto;
import com.example.F5_Backend.entities.Users;
import com.example.F5_Backend.repo.*;
import com.example.F5_Backend.service.UserService;
import com.example.F5_Backend.util.JwtUtil;
import com.example.F5_Backend.util.PasswordUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private GenderRepo genderRepo;

    @Autowired
    private UserRoleRepo userRoleRepo;

    @Autowired
    private StatusRepo statusRepo;

    @Autowired
    private CityRepo cityRepo;

    @Override
    public ResponseEntity<?> setUser(UsersDto usersDto) {
        Map<String, String> errors = new HashMap<>();

        if (usersRepo.existsByEmail(usersDto.getEmail())) {
            errors.put("email", "Email already exists");
        }
        if (usersRepo.existsByMobile(usersDto.getMobile())) {
            errors.put("mobile", "Mobile number already exists");
        }
        if (usersRepo.existsByNic(usersDto.getNic())) {
            errors.put("nic", "NIC already exists");
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        Users users = modelMapper.map(usersDto, Users.class);
        users.setCity(cityRepo.findById(usersDto.getCity_id()).orElseThrow(() -> new RuntimeException("City not found")));
        users.setGender(genderRepo.findById(usersDto.getGender_id()).orElseThrow(() -> new RuntimeException("Gender not found")));
        users.setUserRole(userRoleRepo.findById(3).orElseThrow(() -> new RuntimeException("User Role not found")));
        users.setStatus(statusRepo.findById(1).orElseThrow(() -> new RuntimeException("Status not found")));

        users.setPassword(PasswordUtil.hashPassword(usersDto.getPassword()));

        usersRepo.save(users);
        String token = JwtUtil.createAccessToken(users);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User registration successful"));
    }

    @Override
    public ResponseEntity<?> login(UsersDto usersDto) {
        Users users = usersRepo.findByEmail(usersDto.getEmail()).orElse(null);
        if (users != null && PasswordUtil.checkPassword(usersDto.getPassword(), users.getPassword())) {
            String token = JwtUtil.createAccessToken(users);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "User login successful", "token", token));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }
}
