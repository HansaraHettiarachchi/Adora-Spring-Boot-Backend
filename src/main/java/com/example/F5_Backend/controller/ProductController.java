package com.example.F5_Backend.controller;

import com.example.F5_Backend.dto.ProductDto;
import com.example.F5_Backend.dto.SizeDto;
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

    /**
     * @route POST /api/products/create-product
     * @description Create a new product
     * @access Protected
     * @body { "name": string, "desc": string, "categoryId": int, "motherPlantTypeId": int }
     * @response 201: { "message": "Product created successfully with id: ..." }
     *           400: { "message": "Validation error", "data": { ...fields } }
     *           409: { "message": "Product already exists with this name" }
     */
    @PostMapping("/create-product")
    public ResponseEntity<?> createProduct(@RequestBody(required = false) ProductDto productDto) {

        Map<String, String> errors = validateProduct(productDto);
        if (errors != null) {
            return ResponseEntity.status(211).body(errors);
        }

        return productService.setProducts(productDto);
    }

    /**
     * @route POST /api/products/update-product
     * @description Update an existing product
     * @access Protected
     * @body { "id": int, "name": string, "desc": string, "categoryId": int, "motherPlantTypeId": int }
     * @response 200: { "message": "Product updated successfully with id: ..." }
     *           400: { "message": "Validation error", "data": { ...fields } }
     *           404: { "message": "Product not found with id: ..." }
     *           409: { "message": "Product already exists with this name" }
     */
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

    /**
     * @route GET /api/products/get-all-category
     * @description Get all product categories (optionally filtered by IDs)
     * @access Public
     * @queryParam ids: int[] (optional)
     * @response 200: CategoryDto[]
     */
    @GetMapping("/get-all-category")
    public ResponseEntity<?> getAllCategory(@RequestParam(required = false) List<Integer> ids) {
        return ResponseEntity.ok().body(productService.getCategories(ids));
    }

    /**
     * @route GET /api/products/get-all-mother-plant-type
     * @description Get all mother plant types (optionally filtered by IDs)
     * @access Public
     * @queryParam ids: int[] (optional)
     * @response 200: MotherPlantTypeDto[]
     */
    @GetMapping("/get-all-mother-plant-type")
    public ResponseEntity<?> getAllMotherPlantType(@RequestParam(required = false) List<Integer> ids) {
        return ResponseEntity.ok().body(productService.getMotherPlantTypes(ids));
    }

    /**
     * @route POST /api/products/set-size
     * @description Create a new size
     * @body { "size": string, "shortKey": string }
     * @response 201: { id, size, shortKey }
     *           400: { message: "Validation error" }
     *           409: { message: "Size already exists or constraint violation" }
     */
    @PostMapping("/set-size")
    public ResponseEntity<?> setSize(@RequestBody SizeDto sizeDto) {
        if (sizeDto.getSize() == null || sizeDto.getSize().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("message", "Size cannot be empty"));
        }
        if (sizeDto.getShortKey() == null || sizeDto.getShortKey().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("message", "Short key cannot be empty"));
        }
        return productService.setSize(sizeDto);
    }

    /**
     * @route GET /api/products/get-all-size
     * @description Get all sizes
     * @response 200: [ { id, size, shortKey } ]
     */
    @GetMapping("/get-all-size")
    public ResponseEntity<?> getAllSize() {
        return ResponseEntity.ok(productService.getAllSizes());
    }

    /**
     * @route GET /api/products/get-size-by-id/{id}
     * @description Get size by ID
     * @response 200: { id, size, shortKey }
     *           404: { message: "Size not found" }
     */
    @GetMapping("/get-size-by-id/{id}")
    public ResponseEntity<?> getSizeById(@PathVariable Integer id) {
        return productService.getSizeById(id);
    }

    /**
     * @route DELETE /api/products/delete-size/{id}
     * @description Delete size by ID
     * @response 200: { message: "Size deleted successfully" }
     *           404: { message: "Size not found" }
     *           409: { message: "Cannot delete size due to foreign key constraint" }
     */
    @DeleteMapping("/delete-size/{id}")
    public ResponseEntity<?> deleteSize(@PathVariable Integer id) {
        return productService.deleteSize(id);
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
