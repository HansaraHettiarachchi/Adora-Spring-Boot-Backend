package com.example.F5_Backend.service.impl;

import com.example.F5_Backend.dto.BatchDto;
import com.example.F5_Backend.entities.Batch;
import com.example.F5_Backend.repo.BatchRepo;
import com.example.F5_Backend.repo.ProductRepo;
import com.example.F5_Backend.repo.SizeRepo;
import com.example.F5_Backend.service.BatchService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BatchServiceImpl implements BatchService {
    private final ModelMapper modelMapper;
    private final BatchRepo batchRepo;
    private final ProductRepo productRepo;
    private final SizeRepo sizeRepo;

    @Value("${app.upload.dir}")
    private String relativePath;

    @Override
    public ResponseEntity<?> setBatch(BatchDto batchDto, List<MultipartFile> images) {

        Batch batch = modelMapper.map(batchDto, Batch.class);

        batch.setProduct(productRepo.findById(batchDto.getProductId()).orElse(null));
        batch.setSize(sizeRepo.findById(batchDto.getSizeId()).orElse(null));

        Batch existingBatch = batchRepo.findByProductIdAndSizeIdAndCostAndPrice(batchDto.getProductId(), batchDto.getSizeId(), batchDto.getCost(), batchDto.getPrice());
        saveImages(images, existingBatch != null ? existingBatch.getId() : batch.getProduct().getId());

        if (existingBatch == null) {
            String code;
            Batch existingBatchWithCode = null;

            while (existingBatchWithCode == null) {
                code = RandomStringUtils.randomAlphanumeric(20).toUpperCase();
                existingBatchWithCode = batchRepo.findByCode(code);
            }


            batchRepo.save(batch);
            return ResponseEntity.ok().body("Batch created successfully with id: " + batch.getId());
        } else {
            existingBatch.setQty(existingBatch.getQty() + batchDto.getQty());
            batchRepo.save(existingBatch);
            return ResponseEntity.ok().body("Batch updated successfully with id: " + existingBatch.getId());
        }
    }

    private String saveImages(List<MultipartFile> images, Integer batchId) {
        File dir = new File(relativePath + "/images/productImages/" + batchId + "/");

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
                String filePath = dir.getAbsolutePath() + File.separator + uniqueFileName;

                File dest = new File(filePath);
                image.transferTo(dest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "/uploads/productImages/";
    }

}
