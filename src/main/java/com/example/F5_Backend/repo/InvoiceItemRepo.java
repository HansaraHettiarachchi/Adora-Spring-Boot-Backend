package com.example.F5_Backend.repo;

import com.example.F5_Backend.entities.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepo extends JpaRepository<InvoiceItem, Integer> {
    List<InvoiceItem> findByInvoiceId(Integer invoiceId);
}
