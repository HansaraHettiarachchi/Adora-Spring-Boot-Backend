package com.example.F5_Backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * DTO for {@link com.example.F5_Backend.entities.Product}
 */
@Data
public class ProductDto implements Serializable {
    Integer id;
    @NotNull
    @Size(max = 70)
    String name;

    String desc;

    @NotNull
    MotherPlantTypeDto motherPlantType;

    @NotNull
    CategoryDto category;

    @NotNull
    @JsonProperty("is_active")
    Boolean isActive;

    @JsonProperty("mother_plant_type_id")
    Integer motherPlantTypeId;

    @JsonProperty("category_id")
    Integer categoryId;

}