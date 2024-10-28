package com.spaceships.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spaceships.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername(String username);

}
