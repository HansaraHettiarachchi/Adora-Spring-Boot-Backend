package com.example.F5_Backend.controller;

import com.example.F5_Backend.dto.InvoiceDto;
import com.example.F5_Backend.dto.PaymentMethodDto;
import com.example.F5_Backend.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;


    @PostMapping("/set-invoice")
    public ResponseEntity<?> setInvoice(@RequestBody InvoiceDto invoiceDto, @RequestHeader(value = "Authorization") String token) {
        Map<String, String> errors = validateInvoiceDto(invoiceDto);
        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        return paymentService.setInvoice(invoiceDto, token);
    }

    private Map<String, String> validateInvoiceDto(InvoiceDto invoiceDto) {
        Map<String, String> errors = new HashMap<>();

        if (invoiceDto.getTotal() == null || invoiceDto.getTotal().doubleValue() < 0) {
            errors.put("total", "Total must be a positive value.");
        }
        if (invoiceDto.getQty() == null || invoiceDto.getQty() <= 0) {
            errors.put("qty", "Quantity must be greater than 0.");
        }
        if (invoiceDto.getDatetime() == null) {
            errors.put("datetime", "Datetime is required.");
        }
        if (invoiceDto.getDiscount() != null && invoiceDto.getDiscount().doubleValue() < 0) {
            errors.put("discount", "Discount cannot be negative.");
        }
        if (invoiceDto.getPayment_method_id() == null) {
            errors.put("payment_method_id", "Payment method ID is required.");
        }
        if (invoiceDto.getUsers_id() == null) {
            errors.put("users_id", "User ID is required.");
        }
        if (invoiceDto.getInvoice_items() == null || invoiceDto.getInvoice_items().isEmpty()) {
            errors.put("invoice_items", "At least one invoice item is required.");
        } else {
            for (int i = 0; i < invoiceDto.getInvoice_items().size(); i++) {
                var item = invoiceDto.getInvoice_items().get(i);
                if (item.getBatch_id() == null) {
                    errors.put("invoice_items[" + i + "].batch_id", "Batch ID is required.");
                }
                if (item.getProduct_type_id() == null) {
                    errors.put("invoice_items[" + i + "].product_type_id", "Product type ID is required.");
                }
                if (item.getPrice() == null || item.getPrice().doubleValue() < 0) {
                    errors.put("invoice_items[" + i + "].price", "Price must be a positive value.");
                }
                if (item.getCost() == null || item.getCost().doubleValue() < 0) {
                    errors.put("invoice_items[" + i + "].cost", "Cost must be a positive value.");
                }
                if (item.getQty() == null || item.getQty() <= 0) {
                    errors.put("invoice_items[" + i + "].qty", "Quantity must be greater than 0.");
                }
            }
        }

        return errors;
    }

    /**
     * @route POST /set-payment-method
     * @description Creates a new payment method
     * @access Protected
     */
    @PostMapping("/set-payment-method")

    public ResponseEntity<?> setPaymentMethod(@Valid @RequestBody PaymentMethodDto paymentMethodDto) {
        return paymentService.setPaymentMethod(paymentMethodDto);
    }

    /**
     * @route GET /get-payment-method-by-id/{id}
     * @description Gets payment method by ID
     * @access Protected
     */
    @GetMapping("/get-payment-method-by-id/{id}")
    public ResponseEntity<?> getPaymentMethodById(@PathVariable Integer id) {
        return paymentService.getPaymentMethodById(id);
    }

    /**
     * @route PUT /update-payment-method/{id}
     * @description Updates payment method details
     * @access Protected
     */
    @PutMapping("/update-payment-method/{id}")
    public ResponseEntity<?> updatePaymentMethod(@PathVariable Integer id, @RequestBody PaymentMethodDto updateData) {
        return paymentService.updatePaymentMethod(id, updateData);
    }

    /**
     * @route DELETE /delete-payment-method/{id}
     * @description Deletes a payment method by ID
     * @access Protected
     */
    @DeleteMapping("/delete-payment-method/{id}")
    public ResponseEntity<?> deletePaymentMethod(@PathVariable Integer id) {
        return paymentService.deletePaymentMethod(id);
    }

    /**
     * @route GET /get-all-invoices
     * @description Gets all invoices (paginated)
     * @access Protected
     * @query page: number (required), pageSize: number (required)
     */
    @GetMapping("/get-all-invoices")
    public ResponseEntity<?> getAllInvoices(@RequestParam Integer page, @RequestParam Integer pageSize) {
        return paymentService.getAllInvoices(page, pageSize);
    }
}
