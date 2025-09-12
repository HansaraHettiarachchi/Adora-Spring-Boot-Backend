package com.example.F5_Backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * DTO for {@link com.example.F5_Backend.entities.Category}
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryDto implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 45)
    String name;
}