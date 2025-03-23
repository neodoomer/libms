package com.maids.libms.controller;

import com.maids.libms.model.Patron;
import com.maids.libms.service.PatronService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/patrons")
public class PatronController extends BaseController<Patron, Integer> {
    
    public PatronController(PatronService service) {
        super(service);
    }
} 