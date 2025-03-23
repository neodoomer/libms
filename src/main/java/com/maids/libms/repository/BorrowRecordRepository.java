package com.maids.libms.repository;

import com.maids.libms.model.BorrowRecord;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends BaseRepository<BorrowRecord, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT br FROM BorrowRecord br " +
            "WHERE br.book.id = :bookId AND br.returnDate IS NULL")
    Optional<BorrowRecord> findActiveBorrowByBookId(@Param("bookId") Integer bookId);

    @Query("SELECT br FROM BorrowRecord br " +
            "WHERE br.book.id = :bookId AND br.patron.id = :patronId AND br.returnDate IS NULL")
    Optional<BorrowRecord> findActiveBorrow(@Param("bookId") Integer bookId, @Param("patronId") Integer patronId);
}