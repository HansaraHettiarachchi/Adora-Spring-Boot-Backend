package com.example.F5_Backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.example.F5_Backend.entities.Users}
 */
@Value
public class UsersDto implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 45)
    String fname;

    @NotNull
    @Size(max = 45)
    String lname;

    @NotNull
    @Size(max = 100)
    String address;

    @NotNull
    @Size(max = 20)
    String nic;

    @NotNull
    @Size(max = 100)
    @Email
    String email;

    @Size(max = 100)
    String password;

    @NotNull
    @Size(max = 20)
    String mobile;

    String pImg;

    @NotNull
    Integer user_role_id;

    @NotNull
    Integer gender_id;

    @NotNull
    Integer city_id;

    @NotNull
    Integer status_id;
}