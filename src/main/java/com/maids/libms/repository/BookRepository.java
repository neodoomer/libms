package com.maids.libms.repository;

import com.maids.libms.model.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends BaseRepository<Book, Integer> {
}
