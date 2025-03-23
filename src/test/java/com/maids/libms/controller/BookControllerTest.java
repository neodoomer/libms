package com.maids.libms.controller;

import com.maids.libms.model.Book;
import com.maids.libms.service.BookService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .isbn("978-3-16-148410-0")
                .title("Effective Java")
                .author("Joshua Bloch")
                .publicationYear(2018)
                .build();
        testBook.setId(1);
    }

    @Test
    void findAll_ShouldReturnBooksPaginated() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> expectedPage = new PageImpl<>(List.of(testBook));
        when(bookService.findAll(any(Pageable.class))).thenReturn(expectedPage);

        // Act
        ResponseEntity<?> response = bookController.findAll(pageable, false);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedPage);
        verify(bookService).findAll(pageable);
    }

    @Test
    void findAll_ShouldReturnAllBooksWhenUnpaged() {
        // Arrange
        List<Book> expectedBooks = List.of(testBook);
        when(bookService.findAll()).thenReturn(expectedBooks);

        // Act
        ResponseEntity<?> response = bookController.findAll(Pageable.unpaged(), true);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedBooks);
        verify(bookService).findAll();
    }

    @Test
    void findById_ShouldReturnBookWhenExists() {
        // Arrange
        when(bookService.findById(1)).thenReturn(testBook);

        // Act
        ResponseEntity<Book> response = bookController.findById(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testBook);
        verify(bookService).findById(1);
    }

    @Test
    void findById_ShouldThrowNotFoundWhenDoesNotExist() {
        // Arrange
        when(bookService.findById(999)).thenThrow(new EntityNotFoundException("Book not found"));

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> bookController.findById(999));
        verify(bookService).findById(999);
    }

    @Test
    void create_ShouldReturnCreatedBook() {
        // Arrange
        Book newBook = testBook;
        newBook.setId(null);
        when(bookService.create(any(Book.class))).thenReturn(testBook);

        // Act
        ResponseEntity<Book> response = bookController.create(newBook);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testBook);
        verify(bookService).create(newBook);
    }

    @Test
    void update_ShouldReturnUpdatedBook() {
        // Arrange
        Book updatedBook = testBook;
        updatedBook.setTitle("Effective Java 3rd Edition");
        when(bookService.update(eq(1), any(Book.class))).thenReturn(updatedBook);

        // Act
        ResponseEntity<Book> response = bookController.update(1, updatedBook);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(updatedBook);
        verify(bookService).update(1, updatedBook);
    }

    @Test
    void delete_ShouldReturnSuccessMessage() {
        // Arrange
        when(bookService.delete(1)).thenReturn("Done.");

        // Act
        ResponseEntity<String> response = bookController.delete(1);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Done.");
        verify(bookService).delete(1);
    }
}