package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.Batch;
import com.example.F5_Backend.entities.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface ProductImagesRepo extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findAllByBatch(Batch batch);

    ProductImage findDistinctFirstByBatchId(Integer batchId);
}