package com.example.F5_Backend.service.impl;

import com.example.F5_Backend.dto.CategoryDto;
import com.example.F5_Backend.dto.MotherPlantTypeDto;
import com.example.F5_Backend.dto.ProductDto;
import com.example.F5_Backend.dto.SizeDto;
import com.example.F5_Backend.entities.Category;
import com.example.F5_Backend.entities.MotherPlantType;
import com.example.F5_Backend.entities.Product;
import com.example.F5_Backend.entities.Size;
import com.example.F5_Backend.repo.CategoryRepo;
import com.example.F5_Backend.repo.MotherPlantTypeRepo;
import com.example.F5_Backend.repo.ProductRepo;
import com.example.F5_Backend.repo.SizeRepo;
import com.example.F5_Backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
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
    private final SizeRepo sizeRepo;

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

    @Override
    public ResponseEntity<?> setSize(SizeDto sizeDto) {
        if (sizeDto.getSize() == null || sizeDto.getSize().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Size cannot be empty");
        }
        if (sizeDto.getShortKey() == null || sizeDto.getShortKey().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Short key cannot be empty");
        }
        Size size = modelMapper.map(sizeDto, Size.class);
        try {
            size = sizeRepo.save(size);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Size already exists or constraint violation");
        }
        SizeDto result = modelMapper.map(size, SizeDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Override
    public List<SizeDto> getAllSizes() {
        List<Size> sizes = sizeRepo.findAll();
        return modelMapper.map(sizes, new TypeToken<List<SizeDto>>() {
        }.getType());
    }

    @Override
    public ResponseEntity<?> getSizeById(Integer id) {
        Size size = sizeRepo.findById(id).orElse(null);
        if (size == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Size not found");
        }
        SizeDto result = modelMapper.map(size, SizeDto.class);
        return ResponseEntity.ok(result);
    }

    @Override
    public ResponseEntity<?> deleteSize(Integer id) {
        Size size = sizeRepo.findById(id).orElse(null);
        if (size == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Size not found");
        }
        try {
            sizeRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot delete size due to foreign key constraint");
        }
        return ResponseEntity.ok("Size deleted successfully");
    }

    @Override
    public ResponseEntity<?> setMotherPlantType(MotherPlantTypeDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mother plant type name cannot be empty");
        }
        MotherPlantType entity = modelMapper.map(dto, MotherPlantType.class);
        try {
            entity = motherPlantTypeRepo.save(entity);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Mother plant type already exists or constraint violation");
        }
        MotherPlantTypeDto result = modelMapper.map(entity, MotherPlantTypeDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Override
    public ResponseEntity<?> deleteMotherPlantType(Integer id) {
        MotherPlantType entity = motherPlantTypeRepo.findById(id).orElse(null);
        if (entity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Mother plant type not found");
        }
        try {
            motherPlantTypeRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot delete mother plant type due to foreign key constraint");
        }
        return ResponseEntity.ok("Mother plant type deleted successfully");
    }

}
