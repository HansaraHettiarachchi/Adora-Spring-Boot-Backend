package com.example.F5_Backend.dto.responseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginatedResponse {
    Pagination pagination;
    Integer status;
    List<Object> data;

    static class Pagination {
        Integer page;
        Integer size;
        Integer count;
    }
}
