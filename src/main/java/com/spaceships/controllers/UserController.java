package com.spaceships.controllers;

import java.net.URI;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.spaceships.models.User;
import com.spaceships.repositories.UserRepository;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getUser(@RequestParam(value = "username", required = false) String username) {
        if (username != null)
            return ResponseEntity.ok(userRepository.findByUsername(username));
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable long id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(found.get());
    }

    @PostMapping
    public ResponseEntity<?> postUser(@Valid @RequestBody User user) {

        userRepository.saveAndFlush(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(user.getId())
                .toUri();
        return ResponseEntity.created(location).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putUser(@PathVariable long id, @Valid @RequestBody User user) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        User current = found.get();
        current.setEmail(user.getEmail());
        current.setName(user.getName());
        current.setUsername(user.getUsername());
        current.setPassword(user.getPassword());
        userRepository.saveAndFlush(current);
        return ResponseEntity.ok(current);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        User current = found.get();
        userRepository.delete(current);
        return ResponseEntity.ok(current);
    }

}
