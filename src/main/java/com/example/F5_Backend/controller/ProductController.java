package com.example.F5_Backend.controller;

import com.example.F5_Backend.dto.ProductDto;
import com.example.F5_Backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/create-product")
    public ResponseEntity<?> createProduct(@RequestBody(required = false) ProductDto productDto) {

        Map<String, String> errors = validateProduct(productDto);
        if (errors != null) {
            return ResponseEntity.status(211).body(errors);
        }

        return productService.setProducts(productDto);
    }

    @PostMapping("/update-product")
    public ResponseEntity<?> updateProduct(@RequestBody(required = false) ProductDto productDto) {

        Map<String, String> errors = validateProduct(productDto);

        if (productDto.getId() == null || productDto.getId() == 0) {
            errors.put("id", "Product id cannot be empty or zero");
        }

        if (errors != null) {
            return ResponseEntity.status(211).body(errors);
        }

//        return ResponseEntity.ok().body(productDto);
        return productService.updateProduct(productDto);
    }

    @GetMapping("/get-all-category")
    public ResponseEntity<?> getAllCategory(@RequestParam(required = false) List<Integer> ids) {
        return ResponseEntity.ok().body(productService.getCategories(ids));
    }

    @GetMapping("/get-all-mother-plant-type")
    public ResponseEntity<?> getAllMotherPlantType(@RequestParam(required = false) List<Integer> ids) {
        return ResponseEntity.ok().body(productService.getMotherPlantTypes(ids));
    }

    private Map<String, String> validateProduct(ProductDto productDto) {
        Map<String, String> errors = new HashMap<>();

        if (productDto.getName() == null || productDto.getName().isEmpty()) {
            errors.put("name", "Product name cannot be empty");
        } else if (productDto.getName().length() < 70) {
            errors.put("name", "Product name must be at least 70 characters");
        }

        if (productDto.getDesc() == null || productDto.getDesc().isEmpty()) {
            errors.put("desc", "Product description cannot be empty");
        }

        if (productDto.getCategoryId() == null) {
            errors.put("categoryId", "Product category id cannot be empty");
        }

        if (productDto.getMotherPlantTypeId() == null) {
            errors.put("motherPlantTypeId", "Product mother plant type id cannot be empty");
        }

        if (!errors.isEmpty()) {
            return errors;
        }
        return null;
    }
}
