package com.example.F5_Backend.dto.responseDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginatedResponse<T> {
    Pagination pagination;
    Integer status;
    List<T> data;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Pagination {
        Integer page;
        Integer size;
        Integer matchCount;
        Integer totalCount;
    }
}
