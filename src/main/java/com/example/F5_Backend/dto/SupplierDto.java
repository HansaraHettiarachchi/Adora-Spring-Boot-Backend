package com.example.F5_Backend.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDto {
    private Integer id;
    private String fullname;
    private String company;
    private String address;
    private String mobile;
    private String email;
    private Boolean isActive;
    private Integer statusId;
    private Integer genderId;
    private Integer cityId;
}

