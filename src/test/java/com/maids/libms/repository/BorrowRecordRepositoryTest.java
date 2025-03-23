package com.maids.libms.repository;

import com.maids.libms.model.Book;
import com.maids.libms.model.BorrowRecord;
import com.maids.libms.model.Patron;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BorrowRecordRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BorrowRecordRepository borrowRecordRepository;

    private Book createAndPersistBook() {
        Book book = Book.builder()
                .isbn("978-0-7475-3269-9")
                .title("Harry Potter and the Philosopher's Stone")
                .author("J.K. Rowling")
                .description("The first book in the Harry Potter series")
                .publicationYear(1997)
                .build();
        return entityManager.persist(book);
    }

    private Patron createAndPersistPatron() {
        Patron patron = Patron.builder()
                .name("John Doe")
                .address("123 Main Street")
                .postNo("12345")
                .city("Anytown")
                .email("john.doe@example.com")
                .phoneNo("+1-555-123-4567")
                .build();
        return entityManager.persist(patron);
    }

    @Test
    void shouldSaveBorrowRecord() {
        // given
        Book book = createAndPersistBook();
        Patron patron = createAndPersistPatron();

        LocalDateTime borrowingDate = LocalDateTime.now();

        BorrowRecord borrowRecord = BorrowRecord.builder()
                .book(book)
                .patron(patron)
                .borrowingDate(borrowingDate)
                .build();

        // when
        BorrowRecord savedBorrowRecord = borrowRecordRepository.save(borrowRecord);

        // then
        assertThat(savedBorrowRecord).isNotNull();
        assertThat(savedBorrowRecord.getId()).isNotNull();
        assertThat(savedBorrowRecord.getBorrowingDate()).isEqualTo(borrowingDate);
        assertThat(savedBorrowRecord.getBook().getId()).isEqualTo(book.getId());
        assertThat(savedBorrowRecord.getPatron().getId()).isEqualTo(patron.getId());
    }

    @Test
    void shouldFindBorrowRecordById() {
        // given
        Book book = createAndPersistBook();
        Patron patron = createAndPersistPatron();

        LocalDateTime borrowingDate = LocalDateTime.now();

        BorrowRecord borrowRecord = BorrowRecord.builder()
                .book(book)
                .patron(patron)
                .borrowingDate(borrowingDate)
                .build();

        BorrowRecord savedBorrowRecord = entityManager.persist(borrowRecord);
        entityManager.flush();

        // when
        BorrowRecord foundBorrowRecord = borrowRecordRepository.findById(savedBorrowRecord.getId()).orElse(null);

        // then
        assertThat(foundBorrowRecord).isNotNull();
        assertThat(foundBorrowRecord.getBorrowingDate()).isEqualTo(borrowingDate);
        assertThat(foundBorrowRecord.getBook().getId()).isEqualTo(book.getId());
        assertThat(foundBorrowRecord.getPatron().getId()).isEqualTo(patron.getId());
    }

    @Test
    void shouldDeleteBorrowRecord() {
        // given
        Book book = createAndPersistBook();
        Patron patron = createAndPersistPatron();

        BorrowRecord borrowRecord = BorrowRecord.builder()
                .book(book)
                .patron(patron)
                .borrowingDate(LocalDateTime.now())
                .build();

        BorrowRecord savedBorrowRecord = entityManager.persist(borrowRecord);
        entityManager.flush();

        // when
        borrowRecordRepository.deleteById(savedBorrowRecord.getId());

        // then
        BorrowRecord foundBorrowRecord = entityManager.find(BorrowRecord.class, savedBorrowRecord.getId());
        assertThat(foundBorrowRecord).isNull();
    }

    @Test
    void shouldUpdateBorrowRecord() {
        // given
        Book book = createAndPersistBook();
        Patron patron = createAndPersistPatron();

        LocalDateTime borrowingDate = LocalDateTime.now().minusDays(7);

        BorrowRecord borrowRecord = BorrowRecord.builder()
                .book(book)
                .patron(patron)
                .borrowingDate(borrowingDate)
                .build();

        BorrowRecord savedBorrowRecord = entityManager.persist(borrowRecord);
        entityManager.flush();

        // when
        LocalDateTime returnDate = LocalDateTime.now();
        savedBorrowRecord.setReturnDate(returnDate);
        BorrowRecord updatedBorrowRecord = borrowRecordRepository.save(savedBorrowRecord);

        // then
        assertThat(updatedBorrowRecord.getReturnDate()).isEqualTo(returnDate);

        BorrowRecord foundBorrowRecord = entityManager.find(BorrowRecord.class, savedBorrowRecord.getId());
        assertThat(foundBorrowRecord.getReturnDate()).isEqualTo(returnDate);
    }

    @Test
    void shouldFindAllBorrowRecords() {
        // given
        Book book1 = createAndPersistBook();

        Book book2 = Book.builder()
                .isbn("978-0-7475-3849-3")
                .title("Harry Potter and the Chamber of Secrets")
                .author("J.K. Rowling")
                .description("The second book in the Harry Potter series")
                .publicationYear(1998)
                .build();
        book2 = entityManager.persist(book2);

        Patron patron = createAndPersistPatron();

        BorrowRecord borrowRecord1 = BorrowRecord.builder()
                .book(book1)
                .patron(patron)
                .borrowingDate(LocalDateTime.now().minusDays(14))
                .build();

        BorrowRecord borrowRecord2 = BorrowRecord.builder()
                .book(book2)
                .patron(patron)
                .borrowingDate(LocalDateTime.now().minusDays(7))
                .build();

        entityManager.persist(borrowRecord1);
        entityManager.persist(borrowRecord2);
        entityManager.flush();

        // when
        List<BorrowRecord> borrowRecords = borrowRecordRepository.findAll();

        // then
        assertThat(borrowRecords).isNotEmpty();
        assertThat(borrowRecords.size()).isGreaterThanOrEqualTo(2);

        // Verify that both records are in the returned results
        boolean foundRecord1 = false;
        boolean foundRecord2 = false;

        for (BorrowRecord record : borrowRecords) {
            if (record.getBook().getId().equals(book1.getId()) && record.getPatron().getId().equals(patron.getId())) {
                foundRecord1 = true;
            }
            if (record.getBook().getId().equals(book2.getId()) && record.getPatron().getId().equals(patron.getId())) {
                foundRecord2 = true;
            }
        }

        assertThat(foundRecord1).isTrue();
        assertThat(foundRecord2).isTrue();
    }

    @Test
    void findActiveBorrowByBookId_WhenActiveBorrowExists_ShouldReturnRecord() {
        // Given
        Book book = createAndPersistBook();
        Patron patron = createAndPersistPatron();
        BorrowRecord activeRecord = BorrowRecord.builder()
                .book(book)
                .patron(patron)
                .borrowingDate(LocalDateTime.now())
                .build();
        entityManager.persist(activeRecord);
        entityManager.flush();

        // When
        Optional<BorrowRecord> result = borrowRecordRepository.findActiveBorrowByBookId(book.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getBook().getId()).isEqualTo(book.getId());
        assertThat(result.get().getReturnDate()).isNull();
    }

    @Test
    void findActiveBorrowByBookId_WhenNoActiveBorrow_ShouldReturnEmpty() {
        // Given - book with no borrow records
        Book book = createAndPersistBook();

        // When
        Optional<BorrowRecord> result = borrowRecordRepository.findActiveBorrowByBookId(book.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findActiveBorrow_WhenActiveBorrowExists_ShouldReturnRecord() {
        // Given
        Book book = createAndPersistBook();
        Patron patron = createAndPersistPatron();
        BorrowRecord activeRecord = BorrowRecord.builder()
                .book(book)
                .patron(patron)
                .borrowingDate(LocalDateTime.now())
                .build();
        entityManager.persist(activeRecord);
        entityManager.flush();

        // When
        Optional<BorrowRecord> result = borrowRecordRepository.findActiveBorrow(book.getId(), patron.getId());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getBook().getId()).isEqualTo(book.getId());
        assertThat(result.get().getPatron().getId()).isEqualTo(patron.getId());
        assertThat(result.get().getReturnDate()).isNull();
    }

    @Test
    void findActiveBorrow_WhenReturned_ShouldReturnEmpty() {
        // Given
        Book book = createAndPersistBook();
        Patron patron = createAndPersistPatron();
        BorrowRecord returnedRecord = BorrowRecord.builder()
                .book(book)
                .patron(patron)
                .borrowingDate(LocalDateTime.now().minusDays(5))
                .returnDate(LocalDateTime.now())
                .build();
        entityManager.persist(returnedRecord);
        entityManager.flush();

        // When
        Optional<BorrowRecord> result = borrowRecordRepository.findActiveBorrow(book.getId(), patron.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void findActiveBorrow_WhenNoBorrowExists_ShouldReturnEmpty() {
        // Given
        Book book = createAndPersistBook();
        Patron patron = createAndPersistPatron();

        // When
        Optional<BorrowRecord> result = borrowRecordRepository.findActiveBorrow(book.getId(), patron.getId());

        // Then
        assertThat(result).isEmpty();
    }
}