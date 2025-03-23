package com.maids.libms.service;

import com.maids.libms.model.Book;
import com.maids.libms.model.BorrowRecord;
import com.maids.libms.model.Patron;
import com.maids.libms.repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock
    private BorrowRecordRepository borrowRecordRepository;

    @Mock
    private BookService bookService;

    @Mock
    private PatronService patronService;

    @InjectMocks
    private BorrowService borrowService;

    private final Integer BOOK_ID = 1;
    private final Integer PATRON_ID = 1;
    private Book testBook;
    private Patron testPatron;

    @BeforeEach
    void setUp() {
        testBook = new Book();
        testBook.setId(BOOK_ID);
        testPatron = new Patron();
        testPatron.setId(PATRON_ID);
    }

    @Test
    void borrowBook_Success() {
        // Given
        when(bookService.findById(BOOK_ID)).thenReturn(testBook);
        when(patronService.findById(PATRON_ID)).thenReturn(testPatron);
        when(borrowRecordRepository.findActiveBorrowByBookId(BOOK_ID))
                .thenReturn(Optional.empty());
        when(borrowRecordRepository.save(any(BorrowRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BorrowRecord result = borrowService.borrowBook(BOOK_ID, PATRON_ID);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getBook()).isEqualTo(testBook);
        assertThat(result.getPatron()).isEqualTo(testPatron);
        assertThat(result.getReturnDate()).isNull();
        verify(borrowRecordRepository).save(any(BorrowRecord.class));
    }


    @Test
    void borrowBook_AlreadyBorrowed() {
        // Given
        BorrowRecord existingRecord = new BorrowRecord()
                .setBook(testBook)
                .setPatron(testPatron);

        when(bookService.findById(BOOK_ID)).thenReturn(testBook);
        when(patronService.findById(PATRON_ID)).thenReturn(testPatron);
        when(borrowRecordRepository.findActiveBorrowByBookId(BOOK_ID))
                .thenReturn(Optional.of(existingRecord));

        // When & Then
        assertThatThrownBy(() -> borrowService.borrowBook(BOOK_ID, PATRON_ID))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Book is already borrowed");
    }

    @Test
    void returnBook_Success() {
        // Given
        BorrowRecord activeRecord = new BorrowRecord()
                .setBook(testBook)
                .setPatron(testPatron)
                .setBorrowingDate(LocalDateTime.now());

        when(borrowRecordRepository.findActiveBorrow(BOOK_ID, PATRON_ID))
                .thenReturn(Optional.of(activeRecord));
        when(borrowRecordRepository.save(any(BorrowRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BorrowRecord result = borrowService.returnBook(BOOK_ID, PATRON_ID);

        // Then
        assertThat(result.getReturnDate()).isNotNull();
        assertThat(result.getReturnDate()).isBeforeOrEqualTo(LocalDateTime.now());
        verify(borrowRecordRepository).save(activeRecord);
    }

    @Test
    void returnBook_NoActiveBorrow() {
        // Given
        when(borrowRecordRepository.findActiveBorrow(BOOK_ID, PATRON_ID))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> borrowService.returnBook(BOOK_ID, PATRON_ID))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("No active borrow record found");
    }

    @Test
    void returnBook_ConcurrentUpdate() {
        // Given
        BorrowRecord activeRecord = new BorrowRecord()
                .setBook(testBook)
                .setPatron(testPatron);

        when(borrowRecordRepository.findActiveBorrow(BOOK_ID, PATRON_ID))
                .thenReturn(Optional.of(activeRecord));
        when(borrowRecordRepository.save(activeRecord))
                .thenThrow(new OptimisticLockingFailureException("Concurrent modification"));

        // When & Then
        assertThatThrownBy(() -> borrowService.returnBook(BOOK_ID, PATRON_ID))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void borrowBook_ConcurrencyHandling() {
        // Given
        when(bookService.findById(BOOK_ID)).thenReturn(testBook);
        when(patronService.findById(PATRON_ID)).thenReturn(testPatron);
        when(borrowRecordRepository.findActiveBorrowByBookId(BOOK_ID))
                .thenReturn(Optional.empty())
                .thenReturn(Optional.of(new BorrowRecord())); // Simulate concurrent borrow

        // First borrow should succeed
        borrowService.borrowBook(BOOK_ID, PATRON_ID);

        // Second attempt should fail
        assertThatThrownBy(() -> borrowService.borrowBook(BOOK_ID, PATRON_ID))
                .isInstanceOf(IllegalStateException.class);
    }
}