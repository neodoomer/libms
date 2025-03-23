package com.maids.libms.controller;

import com.maids.libms.model.Book;
import com.maids.libms.service.BookService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController extends BaseController<Book, Integer> {
    
    public BookController(BookService service) {
        super(service);
    }
}
