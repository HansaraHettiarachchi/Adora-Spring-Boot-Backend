package com.example.F5_Backend.service.impl;

import com.example.F5_Backend.dto.UsersDto;
import com.example.F5_Backend.entities.Users;
import com.example.F5_Backend.repo.*;
import com.example.F5_Backend.service.UserService;
import com.example.F5_Backend.util.JwtUtil;
import com.example.F5_Backend.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.View;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepo usersRepo;
    private final ModelMapper modelMapper;
    private final GenderRepo genderRepo;
    private final UserRoleRepo userRoleRepo;
    private final StatusRepo statusRepo;
    private final CityRepo cityRepo;
    private final View error;

    @Value("${app.upload.dir}")
    private String relativePath;

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

    public ResponseEntity<?> updateUser(UsersDto usersDto, MultipartFile image) {
        Users updatedUser = modelMapper.map(usersDto, Users.class);
        Users user = usersRepo.findById(usersDto.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Users existingUser = usersRepo.findByEmail(usersDto.getEmail()).orElse(null);
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already in use");
        }

        existingUser = usersRepo.findByMobile(usersDto.getMobile());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mobile number is already in use");
        }

        existingUser = usersRepo.findByNic(usersDto.getNic());
        if (existingUser != null && !existingUser.getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NIC is already in use");
        }

        user.setFname(updatedUser.getFname());
        user.setLname(updatedUser.getLname());
        user.setEmail(updatedUser.getEmail());
        user.setMobile(updatedUser.getMobile());
        user.setNic(updatedUser.getNic());
        user.setAddress(updatedUser.getAddress());
        user.setGender(genderRepo.findById(usersDto.getGender_id()).get());
        user.setCity(cityRepo.findById(usersDto.getCity_id()).get());

        String imagePath = null;
        try {
            if (image != null && !image.isEmpty()) {
                String fileName = "user_" + usersDto.getId() + "_" + System.currentTimeMillis() + "_" + image.getOriginalFilename();
                imagePath = saveImage(image, fileName);
                user.setPImg(imagePath);
            }
        } catch (IOException e) {
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }

        usersRepo.save(user);
        return ResponseEntity.ok("User updated successfully");
    }


    private String saveImage(MultipartFile image, String fileName) throws IOException {

        File directory = new File(relativePath);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File destinationFile = new File(directory, fileName);
        image.transferTo(destinationFile);

        return "/uploads/f5_backend/profileImages/" + fileName;
    }

}
