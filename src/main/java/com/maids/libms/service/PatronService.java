package com.maids.libms.service;

import com.maids.libms.constant.CacheConstants;
import com.maids.libms.model.Patron;
import com.maids.libms.repository.PatronRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PatronService extends BaseService<Patron, Integer> {
    
    public PatronService(PatronRepository repository) {
        super(repository);
    }

    @Override
    @Cacheable(value = CacheConstants.PATRON_CACHE, key = "#id", unless = "#result == null")
    public Patron findById(Integer id) {
        return super.findById(id);
    }

    @Override
    @CachePut(value = CacheConstants.PATRON_CACHE, key = "#result.id", unless = "#result == null")
    public Patron create(Patron patron) {
        return super.create(patron);
    }

    @Override
    @CacheEvict(value = CacheConstants.PATRON_CACHE, key = "#id")
    public String delete(Integer id) {
        return super.delete(id);
    }

    @Override
    @CachePut(value = CacheConstants.PATRON_CACHE, key = "#id", unless = "#result == null")
    public Patron update(Integer id, Patron patron) {
        return super.update(id, patron);
    }
} 