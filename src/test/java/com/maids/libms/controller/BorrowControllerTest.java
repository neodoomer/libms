package com.maids.libms.controller;

import com.maids.libms.model.BorrowRecord;
import com.maids.libms.service.BorrowService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowControllerTest {

    @Mock
    private BorrowService borrowService;

    @InjectMocks
    private BorrowController borrowController;

    private BorrowRecord testRecord;
    private final Integer BORROW_ID = 1;
    private final Integer BOOK_ID = 1;
    private final Integer PATRON_ID = 1;

    @BeforeEach
    void setUp() {
        testRecord = BorrowRecord.builder()
                .borrowingDate(LocalDateTime.now())
                .build();
        testRecord.setId(BORROW_ID);
    }

    @Test
    void findAll_ShouldReturnRecordsPaginated() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<BorrowRecord> expectedPage = new PageImpl<>(List.of(testRecord));
        when(borrowService.findAll(any(Pageable.class))).thenReturn(expectedPage);

        // Act
        ResponseEntity<?> response = borrowController.findAll(pageable, false);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedPage);
        verify(borrowService).findAll(pageable);
    }

    @Test
    void findAll_ShouldReturnAllRecordsWhenUnpaged() {
        // Arrange
        List<BorrowRecord> expectedRecords = List.of(testRecord);
        when(borrowService.findAll()).thenReturn(expectedRecords);

        // Act
        ResponseEntity<?> response = borrowController.findAll(Pageable.unpaged(), true);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedRecords);
        verify(borrowService).findAll();
    }

    @Test
    void findById_ShouldReturnRecordWhenExists() {
        // Arrange
        when(borrowService.findById(BORROW_ID)).thenReturn(testRecord);

        // Act
        ResponseEntity<BorrowRecord> response = borrowController.findById(BORROW_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testRecord);
        verify(borrowService).findById(BORROW_ID);
    }

    @Test
    void findById_ShouldThrowNotFoundWhenDoesNotExist() {
        // Arrange
        when(borrowService.findById(999)).thenThrow(new EntityNotFoundException("Record not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> borrowController.findById(999));
        verify(borrowService).findById(999);
    }

    @Test
    void create_ShouldReturnCreatedRecord() {
        // Arrange
        BorrowRecord newRecord = testRecord;
        newRecord.setId(null);
        when(borrowService.create(any(BorrowRecord.class))).thenReturn(testRecord);

        // Act
        ResponseEntity<BorrowRecord> response = borrowController.create(newRecord);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testRecord);
        verify(borrowService).create(newRecord);
    }

    @Test
    void update_ShouldReturnUpdatedRecord() {
        // Arrange
        BorrowRecord updatedRecord = testRecord;
        updatedRecord.setReturnDate(LocalDateTime.now());
        when(borrowService.update(eq(BORROW_ID), any(BorrowRecord.class))).thenReturn(updatedRecord);

        // Act
        ResponseEntity<BorrowRecord> response = borrowController.update(BORROW_ID, updatedRecord);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedRecord);
        verify(borrowService).update(BORROW_ID, updatedRecord);
    }

    @Test
    void delete_ShouldReturnSuccessMessage() {
        // Arrange
        when(borrowService.delete(BORROW_ID)).thenReturn("Deleted successfully");

        // Act
        ResponseEntity<String> response = borrowController.delete(BORROW_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Deleted successfully");
        verify(borrowService).delete(BORROW_ID);
    }

    @Test
    void borrowBook_ShouldReturnCreatedRecord() {
        // Arrange
        when(borrowService.borrowBook(BOOK_ID, PATRON_ID)).thenReturn(testRecord);

        // Act
        ResponseEntity<BorrowRecord> response = borrowController.borrowBook(BOOK_ID, PATRON_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testRecord);
        verify(borrowService).borrowBook(BOOK_ID, PATRON_ID);
    }

    @Test
    void borrowBook_ShouldThrowWhenBookNotFound() {
        // Arrange
        when(borrowService.borrowBook(BOOK_ID, PATRON_ID))
                .thenThrow(new EntityNotFoundException("Book not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> borrowController.borrowBook(BOOK_ID, PATRON_ID));
    }

    @Test
    void returnBook_ShouldReturnUpdatedRecord() {
        // Arrange
        testRecord.setReturnDate(LocalDateTime.now());
        when(borrowService.returnBook(BOOK_ID, PATRON_ID)).thenReturn(testRecord);

        // Act
        ResponseEntity<BorrowRecord> response = borrowController.returnBook(BOOK_ID, PATRON_ID);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testRecord);
        verify(borrowService).returnBook(BOOK_ID, PATRON_ID);
    }

    @Test
    void returnBook_ShouldThrowWhenRecordNotFound() {
        // Arrange
        when(borrowService.returnBook(BOOK_ID, PATRON_ID))
                .thenThrow(new EntityNotFoundException("Record not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> borrowController.returnBook(BOOK_ID, PATRON_ID));
    }

    @Test
    void borrowBook_ShouldThrowWhenAlreadyBorrowed() {
        // Arrange
        when(borrowService.borrowBook(BOOK_ID, PATRON_ID))
                .thenThrow(new IllegalStateException("Book already borrowed"));

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> borrowController.borrowBook(BOOK_ID, PATRON_ID));
    }
}