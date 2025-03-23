package com.maids.libms.repository;

import com.maids.libms.model.Patron;
import org.springframework.stereotype.Repository;

@Repository
public interface PatronRepository extends BaseRepository<Patron, Integer> {
}
