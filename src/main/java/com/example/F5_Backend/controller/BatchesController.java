package com.example.F5_Backend.controller;

import com.example.F5_Backend.dto.BatchDto;
import com.example.F5_Backend.service.BatchService;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batches")
public class BatchesController {
    private final Gson gson;
    private final BatchService batchService;

    /**
     * @route POST /api/batches/set-stock
     * @description Set or update stock (batch)
     * @access Protected
     * @body (multipart/form-data)
     *   - images: File[] (batch images)
     *   - data: JSON stringified object matching the BatchDto type
     *     {
     *       "productId": int,
     *       "qty": int,
     *       "cost": double,
     *       "price": double,
     *       "sizeId": int,
     *       "desc": string
     *     }
     * @response 201: { "message": "Batch updated successfully with id: ..." }
     *           400: { "message": "Validation error", "data": { ...fields } }
     *           500: { "message": "Internal server error", "error": "..." }
     */
    @PostMapping("/set-stock")
    private ResponseEntity<?> setStock(@RequestPart(value = "data", required = true) String data,
                                       @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                       @RequestHeader(value = "Authorization") String token) {

        BatchDto batchDto = gson.fromJson(data, BatchDto.class);

        Map<String, String> errors = validateBatch(batchDto);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(211).body(errors);
        }

        return batchService.setBatch(batchDto, images, token);
    }

    private Map<String, String> validateBatch(BatchDto batchDto) {
        Map<String, String> errors = new HashMap<>();

        if (batchDto.getDesc() == null || batchDto.getDesc().isEmpty()) {
            errors.put("desc", "Description cannot be empty");
        } else if (batchDto.getDesc().length() > 255) {
            errors.put("desc", "Description must be at most 255 characters");
        }

        if (batchDto.getPrice() == null || batchDto.getPrice() <= 0) {
            errors.put("price", "Price must be greater than zero");
        }

        if (batchDto.getCost() == null || batchDto.getCost() <= 0) {
            errors.put("cost", "Cost must be greater than zero");
        }

        if (batchDto.getQty() == null || batchDto.getQty() <= 0) {
            errors.put("quantity", "Quantity must be greater than zero");
        }

        if (batchDto.getProductId() == null || batchDto.getProductId() <= 0) {
            errors.put("productId", "Product Id must be greater than zero");
        }

        if (batchDto.getSizeId() == null || batchDto.getSizeId() <= 0) {
            errors.put("sizeId", "Size Id must be greater than zero");
        }

        return errors;
    }

    /**
     * @route DELETE /api/batches/delete-batch/{id}
     * @description Delete batch by ID
     * @access Protected
     * @params
     *   - id: number (required)
     * @response
     *   {
     *     "status": 200,
     *     "message": "Batch deleted successfully"
     *   }
     *   If related order items exist: { "status": 409, "message": "Cannot delete batch: related order items exist" }
     *   If not found: { "status": 404, "message": "Batch not found" }
     *   If error: { "status": 500, "message": "Internal server error", "error": "..." }
     */
    @DeleteMapping("/delete-batch/{id}")
    public ResponseEntity<?> deleteBatch(@PathVariable Integer id) {
        try {
            return batchService.deleteBatch(id);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("status", 500, "message", "Internal server error", "error", e.getMessage()));
        }
    }
}