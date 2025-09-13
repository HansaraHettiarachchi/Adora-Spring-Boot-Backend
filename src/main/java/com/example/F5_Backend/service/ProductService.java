package com.example.F5_Backend.service;

import com.example.F5_Backend.dto.CategoryDto;
import com.example.F5_Backend.dto.MotherPlantTypeDto;
import com.example.F5_Backend.dto.ProductDto;
import com.example.F5_Backend.dto.SizeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ProductService {

    ResponseEntity<?> setProducts(ProductDto productDto);

    List<MotherPlantTypeDto> getMotherPlantTypes(List<Integer> id);

    List<CategoryDto> getCategories(List<Integer> id);

    ResponseEntity<?> updateProduct(ProductDto productDto);

    ResponseEntity<?> setSize(SizeDto sizeDto);

    List<SizeDto> getAllSizes();

    ResponseEntity<?> getSizeById(Integer id);

    ResponseEntity<?> deleteSize(Integer id);

    ResponseEntity<?> setMotherPlantType(MotherPlantTypeDto dto);

    ResponseEntity<?> deleteMotherPlantType(Integer id);

    ResponseEntity<?> setCategory(CategoryDto dto);

    ResponseEntity<?> deleteCategory(Integer id);

    List<ProductDto> getAllProducts();
    ResponseEntity<?> getProductById(Integer id);
    ResponseEntity<?> deleteProduct(Integer id);
}
