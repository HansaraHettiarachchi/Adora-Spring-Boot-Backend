package com.example.F5_Backend.service;

import com.example.F5_Backend.dto.InvoiceDto;
import com.example.F5_Backend.dto.PaymentMethodDto;
import org.springframework.http.ResponseEntity;

public interface PaymentService {

    ResponseEntity<?> setPaymentMethod(PaymentMethodDto paymentMethodDto);

    ResponseEntity<?> getPaymentMethodById(Integer id);

    ResponseEntity<?> updatePaymentMethod(Integer id, PaymentMethodDto updateData);

    ResponseEntity<?> deletePaymentMethod(Integer id);

    ResponseEntity<?> setInvoice(InvoiceDto invoiceDto, String token);

    ResponseEntity<?> getAllInvoices(Integer page, Integer pageSize);
}
