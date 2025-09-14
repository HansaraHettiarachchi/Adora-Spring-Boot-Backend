package com.example.F5_Backend.controller;

import com.example.F5_Backend.dto.ProductDto;
import com.example.F5_Backend.dto.SizeDto;
import com.example.F5_Backend.dto.MotherPlantTypeDto;
import com.example.F5_Backend.dto.CategoryDto;
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
     * 400: { "message": "Validation error", "data": { ...fields } }
     * 409: { "message": "Product already exists with this name" }
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
     * 400: { "message": "Validation error", "data": { ...fields } }
     * 404: { "message": "Product not found with id: ..." }
     * 409: { "message": "Product already exists with this name" }
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
     * @route GET /products
     * @description Get paginated products, each with median price from batches.
     * @queryParam page: number (required)
     * @queryParam pageSize: number (required)
     * @queryParam search: string (optional, matches product name, category name, or mother plant type name)
     * @response 200: {
     *   status: 200,
     *   data: [
     *     {
     *       id: number,
     *       name: string,
     *       desc: string,
     *       mother_plant_type_id: number,
     *       category_id: number,
     *       isActive: boolean,
     *       price: number,
     *       qty: number,
     *       imageUrl: string
     *     }
     *   ],
     *   pagination: {
     *     page: number,
     *     pageSize: number,
     *     total: number
     *   }
     * }
     * @response 500: { status: 500, message: "Internal server error", error: string }
     */
    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize, @RequestParam(required = false) String search) {
        return productService.getProducts(page, pageSize, search);
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
     * 400: { message: "Validation error" }
     * 409: { message: "Size already exists or constraint violation" }
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
     * 404: { message: "Size not found" }
     */
    @GetMapping("/get-size-by-id/{id}")
    public ResponseEntity<?> getSizeById(@PathVariable Integer id) {
        return productService.getSizeById(id);
    }

    /**
     * @route DELETE /api/products/delete-size/{id}
     * @description Delete size by ID
     * @response 200: { message: "Size deleted successfully" }
     * 404: { message: "Size not found" }
     * 409: { message: "Cannot delete size due to foreign key constraint" }
     */
    @DeleteMapping("/delete-size/{id}")
    public ResponseEntity<?> deleteSize(@PathVariable Integer id) {
        return productService.deleteSize(id);
    }

    /**
     * @route POST /api/products/set-mother-plant-type
     * @description Create a new mother plant type
     * @body { "name": string }
     * @response 201: { id, name }
     * 400: { message: "Mother plant type name cannot be empty" }
     * 409: { message: "Mother plant type already exists or constraint violation" }
     */
    @PostMapping("/set-mother-plant-type")
    public ResponseEntity<?> setMotherPlantType(@RequestBody MotherPlantTypeDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("message", "Mother plant type name cannot be empty"));
        }
        return productService.setMotherPlantType(dto);
    }

    /**
     * @route DELETE /api/products/delete-mother-plant-type/{id}
     * @description Delete mother plant type by ID
     * @response 200: { message: "Mother plant type deleted successfully" }
     * 404: { message: "Mother plant type not found" }
     * 409: { message: "Cannot delete mother plant type due to foreign key constraint" }
     */
    @DeleteMapping("/delete-mother-plant-type/{id}")
    public ResponseEntity<?> deleteMotherPlantType(@PathVariable Integer id) {
        return productService.deleteMotherPlantType(id);
    }

    /**
     * @route POST /api/products/set-category
     * @description Create a new category
     * @body { "name": string }
     * @response 201: { id, name }
     * 400: { message: "Category name cannot be empty" }
     * 409: { message: "Category already exists or constraint violation" }
     */
    @PostMapping("/set-category")
    public ResponseEntity<?> setCategory(@RequestBody CategoryDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return ResponseEntity.status(400).body(Map.of("message", "Category name cannot be empty"));
        }
        return productService.setCategory(dto);
    }

    /**
     * @route DELETE /api/products/delete-category/{id}
     * @description Delete category by ID
     * @response 200: { message: "Category deleted successfully" }
     * 404: { message: "Category not found" }
     * 409: { message: "Cannot delete category due to foreign key constraint" }
     */
    @DeleteMapping("/delete-category/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        return productService.deleteCategory(id);
    }

    /**
     * @route GET /api/products/get-all-products
     * @description Get all products
     * @response 200: { status: 200, data: [ ...ProductDto ] }
     */
    @GetMapping("/get-all-products")
    public ResponseEntity<?> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(Map.of("status", 200, "data", products));
    }

    /**
     * @route GET /api/products/get-product-by-id/{id}
     * @description Get product by ID
     * @response 200: { status: 200, data: ProductDto }
     * 400: { status: 400, message: "Invalid product ID", data: null }
     * 404: { status: 404, message: "Product not found", data: null }
     */
    @GetMapping("/get-product-by-id/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    /**
     * @route DELETE /api/products/delete-product/{id}
     * @description Delete product by ID
     * @response 200: { status: 200, message: "Product deleted successfully" }
     * 404: { status: 404, message: "Product not found" }
     */
    @DeleteMapping("/delete-product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) {
        return productService.deleteProduct(id);
    }

    /**
     * @route GET /api/products/product-details/{id}
     * @description Get detailed product info by product ID, including batches and images.
     * @param id: number (required)
     * @response 200: { status: 200, data: { ...product detail... } }
     * 404: { status: 404, message: "Product not found", data: null }
     * 500: { status: 500, message: "Internal server error", error: string }
     */
    @GetMapping("/product-details/{id}")
    public ResponseEntity<?> getProductDetailById(@PathVariable Integer id) {
        return productService.getProductDetailById(id);
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
