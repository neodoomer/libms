package com.maids.libms.repository;

import com.maids.libms.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseRepository<User, Integer> {
    Optional<User> findByEmail(String email);
} 