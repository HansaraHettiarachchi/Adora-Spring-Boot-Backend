package com.example.F5_Backend.service;

import com.example.F5_Backend.dto.CategoryDto;
import com.example.F5_Backend.dto.MotherPlantTypeDto;
import com.example.F5_Backend.dto.ProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {

    ResponseEntity<?> setProducts(ProductDto productDto);

    List<MotherPlantTypeDto> getMotherPlantTypes(List<Integer> id);

    List<CategoryDto> getCategories(List<Integer> id);

    ResponseEntity<?> updateProduct(ProductDto productDto);
}

