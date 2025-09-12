package com.example.F5_Backend.service.impl;

import com.example.F5_Backend.dto.BatchDto;
import com.example.F5_Backend.dto.ProductDto;
import com.example.F5_Backend.dto.SizeDto;
import com.example.F5_Backend.entities.Batch;
import com.example.F5_Backend.entities.Grn;
import com.example.F5_Backend.entities.GrnItem;
import com.example.F5_Backend.entities.ProductImage;
import com.example.F5_Backend.repo.*;
import com.example.F5_Backend.service.BatchService;
import com.example.F5_Backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {
    private final ModelMapper modelMapper;
    private final BatchRepo batchRepo;
    private final ProductRepo productRepo;
    private final SizeRepo sizeRepo;
    private final GrnRepo grnRepo;
    private final GrnItemRepo grnItemRepo;
    private final ProductTypeRepo productTypeRepo;
    private final ProductImagesRepo productImageRepo;
    private final SupplierRepo supplierRepo;
    private final UsersRepo usersRepo;

    @Value("${app.upload.dir}")
    private String relativePath;

    @Override
    public ResponseEntity<?> setBatch(BatchDto batchDto, List<MultipartFile> images, String token) {

        Claims claims = JwtUtil.parse(token.substring(7)).getBody();
        Integer userId = ((Number) claims.get("user_id")).intValue();

        Batch batch = modelMapper.map(batchDto, Batch.class);

//        batch.setProduct(productRepo.findById(batchDto.getProductId()).orElse(null));
//        batch.setSize(sizeRepo.findById(batchDto.getSizeId()).orElse(null));

        Batch existingBatch = batchRepo.findByProductIdAndSizeIdAndCostAndPrice(batchDto.getProductId(), batchDto.getSizeId(), batchDto.getCost(), batchDto.getPrice());
        List<String> fileNames = saveImages(images, existingBatch != null ? existingBatch.getId() : batch.getProduct().getId());

        Grn grn = Grn.builder()
                .date(Instant.now())
                .total(batchDto.getCost())
                .totalQty(batchDto.getQty())
                .users(usersRepo.findById(userId).orElse(null))
                .supplier(supplierRepo.findById(1).orElse(null))
                .build();

        grn = grnRepo.save(grn);

        GrnItem grnItem = GrnItem.builder()
                .cost(batch.getCost())
                .price(batch.getPrice())
                .qty(batch.getQty())
                .productId(batchDto.getProductId())
                .sizeId(batchDto.getSizeId())
                .desc(batchDto.getDesc())
                .grn(grn)
                .productType(productTypeRepo.findById(1).orElse(null))
                .build();

        grnItemRepo.save(grnItem);

        if (existingBatch == null) {
            String code = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
            Batch existingBatchWithCode = batchRepo.findByCode(code);

            while (existingBatchWithCode != null) {
                code = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
                existingBatchWithCode = batchRepo.findByCode(code);
            }

            batch = batchRepo.save(batch);

        } else {
            existingBatch.setQty(existingBatch.getQty() + batchDto.getQty());
            batch = batchRepo.save(existingBatch);
        }

        List<ProductImage> existingImages = productImageRepo.findAllByBatch(batch);
        deleteExistingImages(existingImages);
        productImageRepo.deleteAll(existingImages);

        for (String name : fileNames) {
            ProductImage productImage = ProductImage.builder()
                    .name(name)
                    .batch(batch)
                    .build();
            productImageRepo.save(productImage);
        }
        return ResponseEntity.ok().body("Batch updated successfully with id: " + batch.getId());
    }

    @Override
    public ResponseEntity<?> deleteBatch(Integer id) {
        Batch batch = batchRepo.findById(id).orElse(null);
        if (batch == null) {
            return ResponseEntity.status(404).body(Map.of("status", 404, "message", "Batch not found"));
        }

        try {
            batchRepo.deleteById(id);
            return ResponseEntity.ok(Map.of("status", 200, "message", "Batch deleted successfully"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body(Map.of("status", 409, "message", "Cannot delete batch: related order items exist"));
        }
    }

    @Override
    public ResponseEntity<?> getBatchesByProductId(Integer productId) {
        try {
            List<Batch> batches = batchRepo.findAll();
            List<Map<String, Object>> batchDtos = new ArrayList<>();
            for (Batch batch : batches) {
                if (batch.getProduct() != null && batch.getProduct().getId().equals(productId)) {
                    Map<String, Object> dto = new HashMap<>();
                    dto.put("id", batch.getId());
                    dto.put("qty", batch.getQty());
                    dto.put("price", batch.getPrice());
                    dto.put("cost", batch.getCost());
                    dto.put("desc", batch.getDesc());
                    dto.put("product_id", batch.getProduct().getId());
                    dto.put("size_id", batch.getSize().getId());
                    dto.put("code", batch.getCode());

                    List<ProductImage> images = productImageRepo.findAllByBatch(batch);
                    List<Map<String, Object>> imageDtos = new ArrayList<>();
                    for (ProductImage img : images) {
                        Map<String, Object> imgDto = new HashMap<>();
                        imgDto.put("id", img.getId());
                        imgDto.put("name", img.getName());
                        imgDto.put("batch_id", batch.getId());
                        imageDtos.add(imgDto);
                    }
                    dto.put("product_images", imageDtos);

                    dto.put("product", modelMapper.map(batch.getProduct(), ProductDto.class));

                    dto.put("size", modelMapper.map(batch.getSize(), SizeDto.class));
                    batchDtos.add(dto);
                }
            }
            if (batchDtos.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("status", 404, "message", "No batches found", "data", new ArrayList<>()));
            }
            return ResponseEntity.ok(Map.of("status", 200, "message", "Batches fetched successfully", "data", batchDtos));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("status", 500, "message", "Internal server error"));
        }
    }

    @Override
    public ResponseEntity<?> getBatchById(Integer batchId) {
        try {
            Batch batch = batchRepo.findById(batchId).orElse(null);
            if (batch == null) {
                return ResponseEntity.status(404).body(Map.of("status", 404, "message", "Batch not found", "data", null));
            }
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", batch.getId());
            dto.put("qty", batch.getQty());
            dto.put("price", batch.getPrice());
            dto.put("cost", batch.getCost());
            dto.put("desc", batch.getDesc());
            dto.put("product_id", batch.getProduct().getId());
            dto.put("size_id", batch.getSize().getId());
            dto.put("code", batch.getCode());

            List<ProductImage> images = productImageRepo.findAllByBatch(batch);
            List<Map<String, Object>> imageDtos = new ArrayList<>();
            for (ProductImage img : images) {
                Map<String, Object> imgDto = new java.util.HashMap<>();
                imgDto.put("id", img.getId());
                imgDto.put("name", img.getName());
                imgDto.put("batch_id", batch.getId());
                imageDtos.add(imgDto);
            }
            dto.put("product_images", imageDtos);
            dto.put("product", modelMapper.map(batch.getProduct(), ProductDto.class));
            // Size
            dto.put("size", modelMapper.map(batch.getSize(), SizeDto.class));
            return ResponseEntity.ok(Map.of("status", 200, "message", "Batch fetched successfully", "data", dto));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", 500, "message", "Internal server error", "error", e.getMessage()));
        }
    }

    private void deleteExistingImages(List<ProductImage> productImages) {
        for (ProductImage productImage : productImages) {
            String stringPath = relativePath + productImage.getName().replace("/uploads", "");
            File file = new File(stringPath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    private List<String> saveImages(List<MultipartFile> images, Integer batchId) {
        File dir = new File(relativePath + "/images/productImages/" + batchId + "/");
        List<String> fileNames = new ArrayList<>();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        for (MultipartFile image : images) {
            try {
                String originalFilename = image.getOriginalFilename();
                String extension = "";

                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                } else {
                    extension = ".jpg";
                }

                String uniqueFileName = UUID.randomUUID().toString() + extension;
                String fileName = System.currentTimeMillis() + "_" + uniqueFileName;
                String filePath = dir.getAbsolutePath() + File.separator + fileName;

                File dest = new File(filePath);
                image.transferTo(dest);
                fileNames.add("/uploads/images/productImages/" + batchId + "/" + fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return fileNames;
    }


}
