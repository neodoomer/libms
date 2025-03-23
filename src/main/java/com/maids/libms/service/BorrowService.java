package com.maids.libms.service;

import com.maids.libms.model.Book;
import com.maids.libms.model.BorrowRecord;
import com.maids.libms.model.Patron;
import com.maids.libms.repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BorrowService extends BaseService<BorrowRecord, Integer> {
    private final BookService bookService;
    private final PatronService patronService;
    private final BorrowRecordRepository borrowRecordRepository;

    public BorrowService(
            BorrowRecordRepository repository,
            BookService bookService,
            PatronService patronService) {
        super(repository);
        this.borrowRecordRepository = repository;
        this.bookService = bookService;
        this.patronService = patronService;
    }

    @Transactional
    public BorrowRecord borrowBook(Integer bookId, Integer patronId) {
        // Fetch entities within the transaction
        Book book = bookService.findById(bookId);
        Patron patron = patronService.findById(patronId);

        // Check for active borrow using optimized query
        borrowRecordRepository.findActiveBorrowByBookId(bookId)
                .ifPresent(record -> {
                    throw new IllegalStateException("Book is already borrowed");
                });

        BorrowRecord borrowRecord = new BorrowRecord()
                .setBook(book)
                .setPatron(patron)
                .setBorrowingDate(LocalDateTime.now());

        return borrowRecordRepository.save(borrowRecord);
    }

    @Transactional
    public BorrowRecord returnBook(Integer bookId, Integer patronId) {
        BorrowRecord borrowRecord = borrowRecordRepository.findActiveBorrow(bookId, patronId)
                .orElseThrow(() -> new EntityNotFoundException("No active borrow record found"));

        borrowRecord.setReturnDate(LocalDateTime.now());
        return borrowRecordRepository.save(borrowRecord);
    }
}