package com.example.F5_Backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link com.example.F5_Backend.entities.Invoice}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceDto implements Serializable {
    Integer id;
    @NotNull
    Double total;
    @NotNull
    Double qty;
    @NotNull
    Instant datetime;
    @NotNull
    Double discount;
    @NotNull
    PaymentMethodDto paymentMethod;
    @NotNull
    UsersDto users;
    @NotNull
    OrderStatusDto orderStatus;

    Integer users_id;
    Integer payment_method_id;

    List<InvoiceItemDto> invoice_items;

}
