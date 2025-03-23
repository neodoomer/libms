package com.maids.libms.controller;

import com.maids.libms.model.BorrowRecord;
import com.maids.libms.service.BorrowService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BorrowController extends BaseController<BorrowRecord, Integer> {
    private final BorrowService borrowService;

    public BorrowController(BorrowService service) {
        super(service);
        this.borrowService = service;
    }

    @PostMapping("/borrow/{bookId}/patron/{patronId}")
    public ResponseEntity<BorrowRecord> borrowBook(
            @PathVariable Integer bookId,
            @PathVariable Integer patronId) {
        return ResponseEntity.ok(borrowService.borrowBook(bookId, patronId));
    }

    @PutMapping("/return/{bookId}/patron/{patronId}")
    public ResponseEntity<BorrowRecord> returnBook(
            @PathVariable Integer bookId,
            @PathVariable Integer patronId) {
        return ResponseEntity.ok(borrowService.returnBook(bookId, patronId));
    }
} 