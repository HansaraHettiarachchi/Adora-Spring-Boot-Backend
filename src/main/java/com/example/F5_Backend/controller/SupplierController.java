package com.example.F5_Backend.controller;

import com.example.F5_Backend.dto.SupplierDto;
import com.example.F5_Backend.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/suppliers")
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping("/create-supplier")
    public ResponseEntity<?> createSupplier(@RequestBody SupplierDto supplierDto) {
        Map<String, String> errors = validateSupplier(supplierDto);
        if (!errors.isEmpty()) {
            return ResponseEntity.status(400).body(Map.of(
                "status", 400,
                "message", "Validation error",
                "data", errors
            ));
        }
        return supplierService.createSupplier(supplierDto);
    }

    @PutMapping("/update-supplier/{id}")
    public ResponseEntity<?> updateSupplier(@PathVariable Integer id, @RequestBody SupplierDto supplierDto) {
        return supplierService.updateSupplier(id, supplierDto);
    }

    @GetMapping("/get-supplier-by-id/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable Integer id) {
        return supplierService.getSupplierById(id);
    }

    @GetMapping("/get-all-suppliers")
    public ResponseEntity<?> getAllSuppliers() {
        return supplierService.getAllSuppliers();
    }

    @DeleteMapping("/delete-supplier/{id}")
    public ResponseEntity<?> deleteSupplier(@PathVariable Integer id) {
        return supplierService.deleteSupplier(id);
    }

    private Map<String, String> validateSupplier(SupplierDto dto) {
        Map<String, String> errors = new HashMap<>();
        if (dto.getFullname() == null || dto.getFullname().isBlank()) errors.put("fullname", "Fullname is required");
        if (dto.getCompany() == null || dto.getCompany().isBlank()) errors.put("company", "Company is required");
        if (dto.getAddress() == null || dto.getAddress().isBlank()) errors.put("address", "Address is required");
        if (dto.getEmail() == null || dto.getEmail().isBlank()) errors.put("email", "Email is required");
        if (dto.getMobile() == null || dto.getMobile().isBlank()) errors.put("mobile", "Mobile is required");
        // statusId, genderId, cityId can be validated if needed
        return errors;
    }
}

