package com.example.F5_Backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * DTO for {@link com.example.F5_Backend.entities.Batch}
 */
@AllArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BatchDto implements Serializable {
    Integer id;
    @NotNull
    Integer qty;
    @NotNull
    Double price;
    @NotNull
    Double cost;
    String desc;
//    @NotNull
//    ProductDto product;
//    @NotNull
//    SizeDto size;
    @NotNull
    @Size(max = 30)
    String code;

    Integer sizeId;
    Integer productId;
}