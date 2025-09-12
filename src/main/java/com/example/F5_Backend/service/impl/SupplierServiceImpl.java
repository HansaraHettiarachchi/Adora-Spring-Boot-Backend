package com.example.F5_Backend.service.impl;

import com.example.F5_Backend.dto.SupplierDto;
import com.example.F5_Backend.entities.Supplier;
import com.example.F5_Backend.repo.SupplierRepo;
import com.example.F5_Backend.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepo supplierRepo;
    private final ModelMapper modelMapper;

    @Override
    public ResponseEntity<?> createSupplier(SupplierDto supplierDto) {
        if (supplierRepo.existsByEmail(supplierDto.getEmail())) {
            return ResponseEntity.status(409).body(Map.of("message", "Supplier already exists with this email"));
        }
        if (supplierRepo.existsByMobile(supplierDto.getMobile())) {
            return ResponseEntity.status(409).body(Map.of("message", "Supplier already exists with this mobile"));
        }
        Supplier supplier = modelMapper.map(supplierDto, Supplier.class);
        supplier.setIsActive(supplierDto.getIsActive() != null ? supplierDto.getIsActive() : true);

        supplierRepo.save(supplier);
        SupplierDto responseDto = modelMapper.map(supplier, SupplierDto.class);
        return ResponseEntity.status(201).body(Map.of(
                "status", 201,
                "data", responseDto,
                "message", "Supplier Registered Successfully"
        ));
    }

    @Override
    public ResponseEntity<?> updateSupplier(Integer id, SupplierDto supplierDto) {
        Optional<Supplier> optionalSupplier = supplierRepo.findById(id);
        if (optionalSupplier.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Supplier not found"));
        }
        Supplier supplier = optionalSupplier.get();
        modelMapper.map(supplierDto, supplier);
        supplierRepo.save(supplier);
        SupplierDto responseDto = modelMapper.map(supplier, SupplierDto.class);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "data", responseDto,
                "message", "Supplier Updated Successfully"
        ));
    }

    @Override
    public ResponseEntity<?> getSupplierById(Integer id) {
        Optional<Supplier> supplier = supplierRepo.findById(id);
        if (supplier.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Supplier not found"));
        }
        SupplierDto responseDto = modelMapper.map(supplier.get(), SupplierDto.class);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "data", responseDto,
                "message", "success"
        ));
    }

    @Override
    public ResponseEntity<?> getAllSuppliers() {
        List<Supplier> suppliers = supplierRepo.findAll();
        List<SupplierDto> dtos = suppliers.stream()
                .map(supplier -> modelMapper.map(supplier, SupplierDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "data", dtos,
                "message", "success"
        ));
    }

    @Override
    public ResponseEntity<?> deleteSupplier(Integer id) {
        if (!supplierRepo.existsById(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Supplier not found"));
        }
        supplierRepo.deleteById(id);
        return ResponseEntity.ok(Map.of(
                "status", 200,
                "message", "Supplier Deleted Successfully"
        ));
    }
}
