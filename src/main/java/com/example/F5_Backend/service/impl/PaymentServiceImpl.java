package com.example.F5_Backend.service.impl;

import com.example.F5_Backend.dto.InvoiceDto;
import com.example.F5_Backend.dto.InvoiceItemDto;
import com.example.F5_Backend.dto.PaymentMethodDto;
import com.example.F5_Backend.dto.responseDto.PaginatedResponse;
import com.example.F5_Backend.entities.PaymentMethod;
import com.example.F5_Backend.entities.Invoice;
import com.example.F5_Backend.entities.InvoiceItem;
import com.example.F5_Backend.repo.*;
import com.example.F5_Backend.service.PaymentService;
import com.example.F5_Backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final ModelMapper modelMapper;
    private final PaymentMethodRepo paymentMethodRepo;
    private final InvoiceRepo invoiceRepo;
    private final InvoiceItemRepo invoiceItemRepo;
    private final BatchRepo batchRepo;
    private final UsersRepo usersRepo;
    private final ProductRepo productRepo;
    private final ProductTypeRepo productTypeRepo;
    private final OrderStatusRepo orderStatusRepo;

    @Override
    public ResponseEntity<?> setPaymentMethod(PaymentMethodDto paymentMethodDto) {
        if (paymentMethodDto.getName() == null || paymentMethodDto.getName().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment method name is required");
        }

        PaymentMethod paymentMethod = paymentMethodRepo.findByName(paymentMethodDto.getName());
        if (paymentMethod != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Payment method already exists");
        }

        paymentMethod = modelMapper.map(paymentMethodDto, PaymentMethod.class);
        paymentMethod = paymentMethodRepo.save(paymentMethod);
        PaymentMethodDto resultDto = modelMapper.map(paymentMethod, PaymentMethodDto.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultDto);
    }

    @Override
    public ResponseEntity<?> getPaymentMethodById(Integer id) {
        PaymentMethod paymentMethod = paymentMethodRepo.findById(id).orElse(null);
        if (paymentMethod == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment method not found");
        }

        return ResponseEntity.ok(modelMapper.map(paymentMethod, PaymentMethodDto.class));
    }

    @Override
    public ResponseEntity<?> updatePaymentMethod(Integer id, PaymentMethodDto updateData) {
        Optional<PaymentMethod> optionalPaymentMethod = paymentMethodRepo.findById(id);
        if (optionalPaymentMethod.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment method not found");
        }

        PaymentMethod paymentMethod = optionalPaymentMethod.get();
        String newName = updateData.getName();

        if (newName != null && !newName.trim().isEmpty()) {
            PaymentMethod existing = paymentMethodRepo.findByName(newName.trim());
            if (existing != null && !existing.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Payment method already exists");
            }
            paymentMethod.setName(newName.trim());
        }

        PaymentMethod updated = paymentMethodRepo.save(paymentMethod);
        PaymentMethodDto resultDto = modelMapper.map(updated, PaymentMethodDto.class);
        return ResponseEntity.ok(resultDto);
    }


    @Override
    public ResponseEntity<?> deletePaymentMethod(Integer id) {
        PaymentMethod paymentMethod = paymentMethodRepo.findById(id).orElse(null);
        if (paymentMethod == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Payment method not found");
        }
        paymentMethodRepo.delete(paymentMethod);
        return ResponseEntity.ok("Payment method deleted successfully");
    }

    @Override
    public ResponseEntity<?> setInvoice(InvoiceDto invoiceDto, String token) {

        Claims claims = JwtUtil.parse(token.substring(7)).getBody();
        int user_id = ((Number) claims.get("user_id")).intValue();


        double total = 0.00;
        int qty = 0;
        for (InvoiceItemDto invoiceItemDto : invoiceDto.getInvoice_items()) {
            total += (invoiceItemDto.getPrice() * invoiceItemDto.getQty());
            qty += invoiceItemDto.getQty();
        }

        Invoice invoice = modelMapper.map(invoiceDto, Invoice.class);
        invoice.setTotal(total);
        invoice.setQty(qty);
        invoice.setPaymentMethod(paymentMethodRepo.findById(invoiceDto.getPayment_method_id()).orElse(null));
        invoice.setOrderStatus(orderStatusRepo.findById(1).orElse(null));
        invoice.setUsers(usersRepo.findById(user_id).orElse(null));
        invoice.setDatetime(Instant.now());
        invoice = invoiceRepo.save(invoice);

        for (InvoiceItemDto invoiceItemDto : invoiceDto.getInvoice_items()) {
            InvoiceItem invoiceItem = modelMapper.map(invoiceItemDto, InvoiceItem.class);
            invoiceItem.setBatchId(invoiceItemDto.getBatch_id());
            invoiceItem.setProductType(productTypeRepo.findById(invoiceItemDto.getProduct_type_id()).orElse(null));
            invoiceItem.setInvoice(invoice);
            invoiceItem = invoiceItemRepo.save(invoiceItem);
        }


        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Order Invoiced Successfully", "data", ""));
    }

    @Override
    public ResponseEntity<?> getAllInvoices(Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Invoice> invoicePage = invoiceRepo.findAll(pageable);
        List<Invoice> invoices = invoicePage.getContent();

        List<InvoiceDto> invoiceDtos = new ArrayList<>();
        for (Invoice invoice : invoices) {

            InvoiceDto invoiceDto = modelMapper.map(invoice, InvoiceDto.class);
            invoiceDto.setPayment_method_id(invoiceDto.getPaymentMethod().getId());
            invoiceDto.setUsers_id(invoiceDto.getUsers().getId());
            invoiceDto.setUsers(null);

            List<InvoiceItem> invoiceItems = invoiceItemRepo.findByInvoiceId(invoice.getId());
            List<InvoiceItemDto> invoiceItemDtos = new ArrayList<>();
            for (InvoiceItem item : invoiceItems) {
                InvoiceItemDto itemDto = modelMapper.map(item, InvoiceItemDto.class);
                itemDto.setProduct_type_id(item.getProductType().getId());
                itemDto.setBatch_id(item.getBatchId());
                itemDto.setInvoice(null);
                itemDto.setProduct(null);

                itemDto.setProductType(null);
                itemDto.setBatch(null);
                invoiceItemDtos.add(itemDto);
            }
            invoiceDto.setInvoice_items(invoiceItemDtos);

            invoiceDtos.add(invoiceDto);

        }
        PaginatedResponse.Pagination pagination = PaginatedResponse.Pagination.builder()
                .page(invoicePage.getNumber())
                .size(invoicePage.getSize())
                .matchCount(invoicePage.getNumberOfElements())
                .totalCount((int) invoicePage.getTotalElements())
                .build();

        PaginatedResponse<InvoiceDto> response = PaginatedResponse.<InvoiceDto>builder()
                .data(invoiceDtos)
                .pagination(pagination)
                .build();

        return ResponseEntity.ok(response);
    }
}
