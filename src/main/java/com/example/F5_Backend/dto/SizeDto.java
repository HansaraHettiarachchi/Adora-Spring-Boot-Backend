package com.example.F5_Backend.dto;

import com.example.F5_Backend.entities.Size;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link Size}
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SizeDto implements Serializable {
    private Integer id;
    @NotNull
    @jakarta.validation.constraints.Size(max = 100)
    private String size;
    @NotNull
    @jakarta.validation.constraints.Size(max = 20)
    private String shortKey;
}