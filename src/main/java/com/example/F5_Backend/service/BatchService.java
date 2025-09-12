package com.example.F5_Backend.service;

import com.example.F5_Backend.dto.BatchDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BatchService {

    ResponseEntity<?> setBatch(BatchDto batchDto, List<MultipartFile> images);
}
