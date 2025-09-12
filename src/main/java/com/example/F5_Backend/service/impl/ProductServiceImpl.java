package com.example.F5_Backend.service.impl;

import com.example.F5_Backend.dto.CategoryDto;
import com.example.F5_Backend.dto.MotherPlantTypeDto;
import com.example.F5_Backend.dto.ProductDto;
import com.example.F5_Backend.entities.Category;
import com.example.F5_Backend.entities.MotherPlantType;
import com.example.F5_Backend.entities.Product;
import com.example.F5_Backend.repo.CategoryRepo;
import com.example.F5_Backend.repo.MotherPlantTypeRepo;
import com.example.F5_Backend.repo.ProductRepo;
import com.example.F5_Backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ModelMapper modelMapper;
    private final MotherPlantTypeRepo motherPlantTypeRepo;
    private final CategoryRepo categoryRepo;

    @Override
    public ResponseEntity<?> setProducts(ProductDto productDto) {

        Product product = productRepo.findByName(productDto.getName()).orElse(null);
        if (product != null) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Product already exists with this name");
        }

        product = modelMapper.map(productDto, Product.class);

        product.setCategory(categoryRepo.findById(productDto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found")));
        product.setMotherPlantType(motherPlantTypeRepo.findById(productDto.getMotherPlantTypeId()).orElseThrow(() -> new RuntimeException("Category not found")));
        product.setIsActive(true);


        return ResponseEntity.status(HttpStatus.CREATED).body("Product created successfully with id: " + productRepo.save(product).getId());
    }

    @Override
    public List<MotherPlantTypeDto> getMotherPlantTypes(List<Integer> ids) {
        List<MotherPlantType> categories = (ids != null && !ids.isEmpty())
                ? motherPlantTypeRepo.findAllById(ids)
                : motherPlantTypeRepo.findAll();

        return modelMapper.map(categories, new TypeToken<List<CategoryDto>>() {
        }.getType());
    }

    @Override
    public List<CategoryDto> getCategories(List<Integer> ids) {
        List<Category> categories = (ids != null && !ids.isEmpty())
                ? categoryRepo.findAllById(ids)
                : categoryRepo.findAll();

        return modelMapper.map(categories, new TypeToken<List<CategoryDto>>() {
        }.getType());
    }

    @Override
    public ResponseEntity<?> updateProduct(ProductDto productDto) {
        Product updatedProduct = modelMapper.map(productDto, Product.class);

        Product product = productRepo.findByName(productDto.getName()).orElse(null);
        if (product != null && !product.getId().equals(productDto.getId())) {
            return ResponseEntity.status(HttpStatus.ALREADY_REPORTED).body("Product already exists with this name");
        }

        Product existingProduct = productRepo.findById(productDto.getId()).orElse(null);
        if (existingProduct == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found with id: " + productDto.getId());
        }

        existingProduct.setName(productDto.getName());
        existingProduct.setDesc(productDto.getDesc());
        existingProduct.setCategory(categoryRepo.findById(productDto.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found")));
        existingProduct.setMotherPlantType(motherPlantTypeRepo.findById(productDto.getMotherPlantTypeId()).orElseThrow(() -> new RuntimeException("Category not found")));
        existingProduct.setIsActive(existingProduct.getIsActive());

        productRepo.save(existingProduct);
        return ResponseEntity.status(HttpStatus.OK).body("Product updated successfully with id: " + existingProduct.getId());
    }

}
