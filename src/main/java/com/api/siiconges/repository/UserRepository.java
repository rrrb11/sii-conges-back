package com.api.siiconges.repository;

import java.util.Optional;

import com.api.siiconges.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    boolean existsById(@NonNull Long id);
    
}
