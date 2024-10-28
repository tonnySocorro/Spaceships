package com.spaceships.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.spaceships.models.Spaceship;

public interface SpaceshipRepository extends JpaRepository<Spaceship, Long> {
    List<Spaceship> findByName(String name);

    Page<Spaceship> findByNameContaining(String name, Pageable pageable);

    Page<Spaceship> findAll(Pageable pageable);
}
