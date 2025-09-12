package com.example.F5_Backend.controller;

import com.example.F5_Backend.dto.UsersDto;
import com.example.F5_Backend.service.UserService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final Gson gson;

    @PostMapping("/update-user")
    public ResponseEntity<?> updateUser(@RequestParam(value = "data") String data, @RequestParam(value = "image", required = false) MultipartFile image) {
        UsersDto usersDto = gson.fromJson(data, UsersDto.class);

        Map<String, String> errors = validateUserDto(usersDto);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        return userService.updateUser(usersDto, image);
    }

    @GetMapping("/get-all-cities")
    public ResponseEntity<?> getAllCities(@RequestParam(value = "id", required = false) Integer id) {
        return userService.getAllCities(id);
    }

    @GetMapping("/get-all-genders")
    public ResponseEntity<?> getAllGenders(@RequestParam(value = "id", required = false) Integer id) {
        return userService.getAllGender(id);
    }

    @PostMapping("/set-user")
    public ResponseEntity<?> addUser(@RequestBody UsersDto usersDto) {
        Map<String, String> errors = validateUserDto(usersDto);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }
        return userService.setUser(usersDto);
    }

    private Map<String, String> validateUserDto(UsersDto usersDto) {
        Map<String, String> errors = new HashMap<>();
        if (usersDto.getFname() == null || usersDto.getFname().trim().isEmpty()) {
            errors.put("fname", "First name cannot be empty");
        } else if (usersDto.getFname().length() > 45) {
            errors.put("fname", "First name must be at most 45 characters");
        }
        if (usersDto.getLname() == null || usersDto.getLname().trim().isEmpty()) {
            errors.put("lname", "Last name cannot be empty");
        } else if (usersDto.getLname().length() > 45) {
            errors.put("lname", "Last name must be at most 45 characters");
        }
        if (usersDto.getAddress() == null || usersDto.getAddress().trim().isEmpty()) {
            errors.put("address", "Address cannot be empty");
        } else if (usersDto.getAddress().length() > 100) {
            errors.put("address", "Address must be at most 100 characters");
        }
        if (usersDto.getNic() == null || usersDto.getNic().trim().isEmpty()) {
            errors.put("nic", "NIC cannot be empty");
        } else if (usersDto.getNic().length() > 20) {
            errors.put("nic", "NIC must be at most 20 characters");
        }
        if (usersDto.getEmail() == null || usersDto.getEmail().trim().isEmpty()) {
            errors.put("email", "Email cannot be empty");
        } else if (usersDto.getEmail().length() > 100) {
            errors.put("email", "Email must be at most 100 characters");
        } else if (!usersDto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.put("email", "Please provide a valid email");
        }

        if (usersDto.getPassword() == null || usersDto.getPassword().trim().isEmpty()) {
            errors.put("mobile", "Password cannot be empty");
        } else if (usersDto.getPassword().length() > 100) {
            errors.put("password", "Password must be at most 100 characters");
        }

        if (usersDto.getMobile() == null || usersDto.getMobile().trim().isEmpty()) {
            errors.put("mobile", "Mobile number cannot be empty");
        } else if (usersDto.getMobile().length() > 20) {
            errors.put("mobile", "Mobile number must be at most 20 characters");
        }
        if (usersDto.getGender_id() == null) {
            errors.put("gender_id", "Gender must be selected");
        }
        if (usersDto.getCity_id() == null) {
            errors.put("city_id", "City must be selected");
        }
        return errors;
    }

    private Map<String, String> validateLoginDto(UsersDto usersDto) {
        Map<String, String> errors = new HashMap<>();
        if (usersDto.getEmail() == null || usersDto.getEmail().trim().isEmpty()) {
            errors.put("email", "Email cannot be empty");
        } else if (!usersDto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            errors.put("email", "Please provide a valid email");
        }
        if (usersDto.getPassword() == null || usersDto.getPassword().trim().isEmpty()) {
            errors.put("password", "Password cannot be empty");
        } else if (usersDto.getPassword().length() > 100) {
            errors.put("password", "Password must be at most 100 characters");
        }
        return errors;
    }
}
