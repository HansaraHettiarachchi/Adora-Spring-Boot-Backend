package com.example.F5_Backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "Size")
@Table(name = "size")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @jakarta.validation.constraints.Size(max = 100)
    @NotNull
    @Column(name = "size", nullable = false, length = 100)
    private String size;

    @jakarta.validation.constraints.Size(max = 20)
    @NotNull
    @Column(name = "short_key", nullable = false, length = 20)
    private String shortKey;

}