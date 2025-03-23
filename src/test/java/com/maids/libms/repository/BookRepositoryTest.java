package com.maids.libms.repository;

import com.maids.libms.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldSaveBook() {
        // given
        Book book = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Philosopher's Stone")
                .author("J.K. Rowling")
                .description("The first book in the Harry Potter series")
                .publicationYear(1997)
                .build();

        // when
        Book savedBook = bookRepository.save(book);

        // then
        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    void shouldFindBookById() {
        // given
        Book book = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Philosopher's Stone")
                .author("J.K. Rowling")
                .description("The first book in the Harry Potter series")
                .publicationYear(1997)
                .build();
        
        Book savedBook = entityManager.persist(book);
        entityManager.flush();

        // when
        Book foundBook = bookRepository.findById(savedBook.getId()).orElse(null);

        // then
        assertThat(foundBook).isNotNull();
        assertThat(foundBook.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    void shouldDeleteBook() {
        // given
        Book book = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Philosopher's Stone")
                .author("J.K. Rowling")
                .description("The first book in the Harry Potter series")
                .publicationYear(1997)
                .build();
        
        Book savedBook = entityManager.persist(book);
        entityManager.flush();

        // when
        bookRepository.deleteById(savedBook.getId());

        // then
        Book foundBook = entityManager.find(Book.class, savedBook.getId());
        assertThat(foundBook).isNull();
    }

    @Test
    void shouldUpdateBook() {
        // given
        Book book = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Philosopher's Stone")
                .author("J.K. Rowling")
                .description("The first book in the Harry Potter series")
                .publicationYear(1997)
                .build();
        
        Book savedBook = entityManager.persist(book);
        entityManager.flush();
        
        // when
        savedBook.setTitle("Harry Potter and the Sorcerer's Stone");
        Book updatedBook = bookRepository.save(savedBook);
        
        // then
        assertThat(updatedBook.getTitle()).isEqualTo("Harry Potter and the Sorcerer's Stone");
        
        Book foundBook = entityManager.find(Book.class, savedBook.getId());
        assertThat(foundBook.getTitle()).isEqualTo("Harry Potter and the Sorcerer's Stone");
    }

    @Test
    void shouldFindAllBooks() {
        // given
        Book book1 = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Philosopher's Stone")
                .author("J.K. Rowling")
                .description("The first book in the Harry Potter series")
                .publicationYear(1997)
                .build();
        
        Book book2 = Book.builder()
                .isbn("978-0-7475-3849-3")
                .title("Harry Potter and the Chamber of Secrets")
                .author("J.K. Rowling")
                .description("The second book in the Harry Potter series")
                .publicationYear(1998)
                .build();
        
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();
        
        // when
        List<Book> books = bookRepository.findAll();
        
        // then
        assertThat(books).isNotEmpty();
        assertThat(books.size()).isGreaterThanOrEqualTo(2);
        assertThat(books).extracting(Book::getIsbn).contains(book1.getIsbn(), book2.getIsbn());
    }
} 