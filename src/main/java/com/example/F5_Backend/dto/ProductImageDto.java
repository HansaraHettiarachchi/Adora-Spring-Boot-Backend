package com.example.F5_Backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * DTO for {@link com.example.F5_Backend.entities.ProductImage}
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductImageDto implements Serializable {
    Integer id;
    @NotNull
    String name;
    @NotNull
    BatchDto batch;
}