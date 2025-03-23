package com.maids.libms.controller;

import com.maids.libms.model.BaseModel;
import com.maids.libms.service.BaseService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.List;

public abstract class BaseController<T extends BaseModel<ID>, ID extends Serializable> {
    protected final BaseService<T, ID> service;

    protected BaseController(BaseService<T, ID> service) {
        this.service = service;
    }

    @GetMapping()
    public ResponseEntity<?> findAll(Pageable pageable,
                                   @RequestParam(required = false, defaultValue = "false") boolean unpaged) {
        if (unpaged) {
            List<T> entities = service.findAll();
            return ResponseEntity.ok(entities);
        }
        Page<T> entities = service.findAll(pageable);
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<T> findById(@PathVariable ID id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<T> create(@Valid @RequestBody T entity) {
        return ResponseEntity.ok(service.create(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<T> update(@PathVariable ID id, @Valid @RequestBody T entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable ID id) {
        return ResponseEntity.ok(service.delete(id));
    }
} 