package com.maids.libms.service;

import com.maids.libms.constant.CacheConstants;
import com.maids.libms.model.Book;
import com.maids.libms.repository.BookRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BookService extends BaseService<Book, Integer> {
    
    public BookService(BookRepository repository) {
        super(repository);
    }

    @Override
    @Cacheable(value = CacheConstants.BOOK_CACHE, key = "#id", unless = "#result == null")
    public Book findById(Integer id) {
        return super.findById(id);
    }

    @Override
    @CachePut(value = CacheConstants.BOOK_CACHE, key = "#result.id", unless = "#result == null")
    public Book create(Book book) {
        return super.create(book);
    }

    @Override
    @CacheEvict(value = CacheConstants.BOOK_CACHE, key = "#id")
    public String delete(Integer id) {
        return super.delete(id);
    }

    @Override
    @CachePut(value = CacheConstants.BOOK_CACHE, key = "#id", unless = "#result == null")
    public Book update(Integer id, Book book) {
        return super.update(id, book);
    }
}