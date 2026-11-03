package com.skyray16.userservice.repository;

import com.skyray16.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring сам сгенерирует SQL-запрос по названию метода!
    Optional<User> findByEmail(String email);
}