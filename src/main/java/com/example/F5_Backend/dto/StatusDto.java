package com.example.F5_Backend.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.example.F5_Backend.entities.Status}
 */
@Value
public class StatusDto implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 10)
    @NotEmpty
    String name;
}