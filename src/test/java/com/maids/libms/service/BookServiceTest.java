package com.maids.libms.service;

import com.maids.libms.constant.CacheConstants;
import com.maids.libms.model.Book;
import com.maids.libms.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book testBook;

    @BeforeEach
    void setUp() {
        testBook = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Philosopher's Stone")
                .author("J.K. Rowling")
                .description("The first book in the Harry Potter series")
                .publicationYear(1997)
                .build();
        testBook.setId(1);
    }

    @Test
    void findById_shouldReturnBook_whenBookExists() {
        // given
        when(bookRepository.findById(1)).thenReturn(Optional.of(testBook));

        // when
        Book result = bookService.findById(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testBook.getId());
        assertThat(result.getTitle()).isEqualTo(testBook.getTitle());
        verify(bookRepository, times(1)).findById(1);
    }

    @Test
    void findById_shouldThrowException_whenBookDoesNotExist() {
        // given
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> bookService.findById(999));
        verify(bookRepository, times(1)).findById(999);
    }

    @Test
    void create_shouldReturnSavedBook() {
        // given
        Book bookToCreate = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Philosopher's Stone")
                .author("J.K. Rowling")
                .publicationYear(1997)
                .build();

        when(bookRepository.save(any(Book.class))).thenReturn(testBook);

        // when
        Book result = bookService.create(bookToCreate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testBook.getId());
        verify(bookRepository, times(1)).save(bookToCreate);
    }

    @Test
    void update_shouldReturnUpdatedBook_whenBookExists() {
        // given
        Book bookToUpdate = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Sorcerer's Stone") // Updated title
                .author("J.K. Rowling")
                .publicationYear(1997)
                .build();
        bookToUpdate.setId(1);

        Book updatedBook = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Sorcerer's Stone")
                .author("J.K. Rowling")
                .publicationYear(1997)
                .build();
        updatedBook.setId(1);

        when(bookRepository.findById(1)).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        // when
        Book result = bookService.update(1, bookToUpdate);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Harry Potter and the Sorcerer's Stone");
        verify(bookRepository, times(1)).findById(1);
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void update_shouldThrowException_whenBookDoesNotExist() {
        // given
        Book bookToUpdate = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Sorcerer's Stone")
                .author("J.K. Rowling")
                .publicationYear(1997)
                .build();

        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> bookService.update(999, bookToUpdate));
        verify(bookRepository, times(1)).findById(999);
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void delete_shouldDeleteBook_whenBookExists() {
        // given
        when(bookRepository.findById(1)).thenReturn(Optional.of(testBook));
        doNothing().when(bookRepository).delete(any(Book.class));

        // when
        String result = bookService.delete(1);

        // then
        assertThat(result).isEqualTo("Done.");
        verify(bookRepository, times(1)).findById(1);
        verify(bookRepository, times(1)).delete(testBook);
    }

    @Test
    void delete_shouldThrowException_whenBookDoesNotExist() {
        // given
        when(bookRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThrows(EntityNotFoundException.class, () -> bookService.delete(999));
        verify(bookRepository, times(1)).findById(999);
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    void findAll_shouldReturnAllBooks() {
        // given
        Book book2 = Book.builder()
                .isbn("978-0-7475-3849-3")
                .title("Harry Potter and the Chamber of Secrets")
                .author("J.K. Rowling")
                .publicationYear(1998)
                .build();
        book2.setId(2);

        List<Book> books = Arrays.asList(testBook, book2);
        when(bookRepository.findAll()).thenReturn(books);

        // when
        List<Book> result = bookService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).contains(testBook, book2);
        verify(bookRepository, times(1)).findAll();
    }


} 