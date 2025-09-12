package com.example.F5_Backend.service;

import com.example.F5_Backend.dto.SupplierDto;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface SupplierService {
    ResponseEntity<?> createSupplier(SupplierDto supplierDto);
    ResponseEntity<?> updateSupplier(Integer id, SupplierDto supplierDto);
    ResponseEntity<?> getSupplierById(Integer id);
    ResponseEntity<?> getAllSuppliers();
    ResponseEntity<?> deleteSupplier(Integer id);
}

