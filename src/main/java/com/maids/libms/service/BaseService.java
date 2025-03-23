package com.maids.libms.service;

import com.maids.libms.model.BaseModel;
import com.maids.libms.repository.BaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.io.Serializable;
import java.util.List;

public abstract class BaseService<Entity extends BaseModel<Id>, Id extends Serializable> {
    protected final BaseRepository<Entity, Id> repository;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public BaseService(BaseRepository<Entity, Id> repository) {
        this.repository = repository;
    }

    public Entity findById(Id id) {
        log.debug("Fetching entity with id {} from database", id);
        String message = "Resource: " + this.getClass().getSimpleName() + " with id " + id + " does not exist";
        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Entity not found: {}", message);
                    return new EntityNotFoundException(message);
                });
    }

    public List<Entity> findAll() {
        log.debug("Fetching all entities from database");
        return repository.findAll();
    }

    public Page<Entity> findAll(Pageable pageable) {
        log.debug("Fetching paginated entities from database");
        return repository.findAll(pageable);
    }

    public Entity create(Entity resource) {
        log.debug("Creating new entity: {}", resource);
        return repository.save(resource);
    }

    public String delete(Id id) {
        log.debug("Deleting entity with id: {}", id);
        Entity resource = findById(id);
        repository.delete(resource);
        log.info("Entity with id {} deleted successfully", id);
        return "Done.";
    }

    public Entity update(Id id, Entity resource) {
        log.debug("Updating entity with id: {}", id);
        Entity existingEntity = findById(id);
        resource.setId(id);
        return repository.save(resource);
    }
}