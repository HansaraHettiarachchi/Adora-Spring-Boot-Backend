package com.example.F5_Backend.dto;

import com.example.F5_Backend.entities.Size;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * DTO for {@link Size}
 */
@AllArgsConstructor
@Getter
public class SizeDto implements Serializable {
    private final Integer id;
    @NotNull
    @jakarta.validation.constraints.Size(max = 100)
    private final String size;
    @NotNull
    @jakarta.validation.constraints.Size(max = 20)
    private final String shortKey;
}