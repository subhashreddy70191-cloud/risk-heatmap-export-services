package com.riskheatmap.repository;

import com.riskheatmap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findByUsernameIn(Collection<String> usernames);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
