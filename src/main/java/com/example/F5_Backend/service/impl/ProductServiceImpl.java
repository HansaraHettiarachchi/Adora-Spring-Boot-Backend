package com.example.F5_Backend.service.impl;

import com.example.F5_Backend.dto.CategoryDto;
import com.example.F5_Backend.dto.MotherPlantTypeDto;
import com.example.F5_Backend.dto.ProductDto;
import com.example.F5_Backend.dto.SizeDto;
import com.example.F5_Backend.dto.responseDto.PaginatedResponse;
import com.example.F5_Backend.entities.*;
import com.example.F5_Backend.repo.*;
import com.example.F5_Backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final ModelMapper modelMapper;
    private final MotherPlantTypeRepo motherPlantTypeRepo;
    private final CategoryRepo categoryRepo;
    private final SizeRepo sizeRepo;
    private final BatchRepo batchRepo;
    private final ProductImagesRepo productImagesRepo;

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

    @Override
    public ResponseEntity<?> setCategory(CategoryDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Category name cannot be empty");
        }
        Category entity = modelMapper.map(dto, Category.class);
        try {
            entity = categoryRepo.save(entity);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Category already exists or constraint violation");
        }
        CategoryDto result = modelMapper.map(entity, CategoryDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Override
    public ResponseEntity<?> deleteCategory(Integer id) {
        Category entity = categoryRepo.findById(id).orElse(null);
        if (entity == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found");
        }
        try {
            categoryRepo.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Cannot delete category due to foreign key constraint");
        }
        return ResponseEntity.ok("Category deleted successfully");
    }

    @Override
    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepo.findAll();
        return modelMapper.map(products, new TypeToken<List<ProductDto>>() {
        }.getType());
    }

    @Override
    public ResponseEntity<?> getProductById(Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", 400, "message", "Invalid product ID"));
        }
        Product product = productRepo.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", 404, "message", "Product not found"));
        }
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        return ResponseEntity.ok(Map.of("status", 200, "data", productDto));
    }

    @Override
    public ResponseEntity<?> deleteProduct(Integer id) {
        Product product = productRepo.findById(id).orElse(null);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", 404, "message", "Product not found"));
        }
        productRepo.deleteById(id);
        return ResponseEntity.ok(Map.of("status", 200, "message", "Product deleted successfully"));
    }

    @Override
    public ResponseEntity<?> getProducts(Integer page, Integer size, String search) {

        List<Object> result = new ArrayList<>();
        List<Product> products;
        Page<Product> pagedProducts = null;

        boolean isPaginated = (page != null && size != null);
        boolean hasSearch = (search != null && !search.trim().isEmpty());

        if (isPaginated) {
            PageRequest pageRequest = PageRequest.of(page, size);
            if (hasSearch) {
                pagedProducts = productRepo.searchActiveProducts(true, search.trim(), pageRequest);
            } else {
                pagedProducts = productRepo.findByIsActive(true, pageRequest);
            }
            products = pagedProducts.getContent();
        } else {
            if (hasSearch) {
                products = productRepo.searchActiveProducts(true, search.trim());
            } else {
                products = productRepo.findByIsActive(true);
            }
        }

        for (Product product : products) {
            HashMap<String, Object> data = new HashMap<>();

            data.put("id", product.getId());
            data.put("name", product.getName());
            data.put("desc", product.getDesc());
            data.put("mother_plant_type_id", product.getMotherPlantType() != null ? product.getMotherPlantType().getId() : null);
            data.put("category_id", product.getCategory() != null ? product.getCategory().getId() : null);
            data.put("isActive", product.getIsActive());

            Batch batch = null;
            if (product.getSelectedShowingBatchId() != null) {
                batch = batchRepo.findById(product.getSelectedShowingBatchId()).orElse(null);
            }
            if (batch == null) {
                batch = batchRepo.findDistinctFirstByProduct(product);

                if (batch == null) {
                    continue;
                }
            }

            Integer totalQty = batchRepo.findTotalQtyOfProductId(product.getId());

            data.put("price", batch != null ? batch.getPrice() : null);
            data.put("qty", totalQty);
            ProductImage productImage = (batch != null) ? productImagesRepo.findDistinctFirstByBatchId(batch.getId()) : null;
            data.put("imageUrl", productImage != null ? productImage.getName() : null);

            result.add(data);
        }

        PaginatedResponse.Pagination pagination = PaginatedResponse.Pagination.builder()
                .page(isPaginated ? pagedProducts.getNumber() : 0)
                .size(isPaginated ? pagedProducts.getSize() : result.size())
                .matchCount(isPaginated ? pagedProducts.getNumberOfElements() : result.size())
                .totalCount(isPaginated ? (int) pagedProducts.getTotalElements() : result.size())
                .build();

        PaginatedResponse paginatedResponse = PaginatedResponse.builder()
                .status(200)
                .data(result)
                .pagination(pagination)
                .build();

        return ResponseEntity.ok(paginatedResponse);
    }

    @Override
    public ResponseEntity<?> getProductDetailById(Integer id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", 400, "message", "Invalid product ID", "data", null));
            }
            Product product = productRepo.findById(id).orElse(null);
            if (product == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status", 404, "message", "Product not found", "data", null));
            }
            Map<String, Object> data = new HashMap<>();
            data.put("id", product.getId());
            data.put("name", product.getName());
            data.put("desc", product.getDesc());
            if (product.getMotherPlantType() != null) {
                Map<String, Object> mpt = new HashMap<>();
                mpt.put("id", product.getMotherPlantType().getId());
                mpt.put("name", product.getMotherPlantType().getName());
                data.put("mother_plant_type", mpt);
            } else {
                data.put("mother_plant_type", null);
            }
            if (product.getCategory() != null) {
                Map<String, Object> cat = new HashMap<>();
                cat.put("id", product.getCategory().getId());
                cat.put("name", product.getCategory().getName());
                data.put("category", cat);
            } else {
                data.put("category", null);
            }
            data.put("isActive", product.getIsActive());
            // Batches
            List<Batch> batches = batchRepo.findByProductId(product.getId());
            List<Map<String, Object>> batchList = new ArrayList<>();
            for (Batch batch : batches) {
                Map<String, Object> batchMap = new HashMap<>();
                batchMap.put("id", batch.getId());
                batchMap.put("qty", batch.getQty());
                batchMap.put("price", batch.getPrice());
                batchMap.put("cost", batch.getCost());
                batchMap.put("desc", batch.getDesc());
                batchMap.put("size_id", batch.getSize() != null ? batch.getSize().getId() : null);
                batchMap.put("code", batch.getCode());
                // Images
                List<ProductImage> images = productImagesRepo.findAllByBatch(batch);
                List<Map<String, Object>> imageList = new ArrayList<>();
                for (ProductImage img : images) {
                    Map<String, Object> imgMap = new HashMap<>();
                    imgMap.put("id", img.getId());
                    imgMap.put("name", img.getName());
                    imgMap.put("batch_id", batch.getId());
                    imageList.add(imgMap);
                }
                batchMap.put("images", imageList);
                batchList.add(batchMap);
            }
            data.put("batches", batchList);
            return ResponseEntity.ok(Map.of("status", 200, "data", data));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", 500, "message", "Internal server error", "error", e.getMessage()));
        }
    }

}
