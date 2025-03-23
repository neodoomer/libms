package com.maids.libms.repository;

import com.maids.libms.model.BaseModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository<T extends BaseModel<Id>, Id extends Serializable> extends JpaRepository<T, Id> {
}
