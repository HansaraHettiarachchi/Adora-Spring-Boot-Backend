package com.example.F5_Backend.dto;

import com.example.F5_Backend.entities.Batch;
import com.example.F5_Backend.entities.Product;
import com.example.F5_Backend.entities.ProductType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

/**
 * DTO for {@link com.example.F5_Backend.entities.InvoiceItem}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceItemDto implements Serializable {
    Integer id;
    @NotNull
    Double price;
    @NotNull
    Double cost;
    @NotNull
    Product product;
    @NotNull
    Integer qty;

    @NotNull
    Batch batch;

    @NotNull
    InvoiceDto invoice;
    @NotNull
    ProductType productType;

    Integer product_type_id;
    Integer batch_id;
}