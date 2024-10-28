package com.spaceships.controllers;

import org.springframework.http.HttpStatus;
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
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;

import com.spaceships.exceptions.SpaceshipNotFoundException;
import com.spaceships.models.Spaceship;
import com.spaceships.repositories.SpaceshipRepository;
import com.spaceships.utils.JwtTokenUtil;

import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/spaceships")
public class SpaceshipsController {
    private final SpaceshipRepository spaceshipRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public SpaceshipsController(SpaceshipRepository spaceshipRepository, JwtTokenUtil jwtTokenUtil) {
        this.spaceshipRepository = spaceshipRepository;
        this.jwtTokenUtil = jwtTokenUtil;

    }

    @GetMapping
    public ResponseEntity<?> getSpaceships(@RequestParam(value = "name", required = false) String name,
            Pageable pageable) {
        if (name != null)
            return ResponseEntity.ok(spaceshipRepository.findByNameContaining(name, pageable));
        return ResponseEntity.ok(spaceshipRepository.findAll(pageable));
    }

    @Operation(summary = "Get a spaceship by its id")
    @ApiResponse(responseCode = "200", description = "Found the spaceship", content = @Content(mediaType = "application/json"))
    @Cacheable("spaceshipsCache")
    @GetMapping("/{id}")
    public ResponseEntity<Spaceship> getSpaceship(@PathVariable long id) {
        Spaceship found = spaceshipRepository.findById(id)
                .orElseThrow(() -> new SpaceshipNotFoundException("Spaceship not found with id: " + id));
        return ResponseEntity.ok(found);
    }

    @PostMapping
    public ResponseEntity<?> postSpaceship(HttpServletRequest request, @Valid @RequestBody Spaceship spaceship) {

        spaceshipRepository.saveAndFlush(spaceship);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(spaceship.getId())
                .toUri();
        return ResponseEntity.created(location).body(spaceship);
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "spaceshipsCache", allEntries = true)
    public ResponseEntity<?> putSpaceship(HttpServletRequest request, @PathVariable long id,
            @Valid @RequestBody Spaceship spaceship) {
        String token = request.getHeader("Authorization"); // Obtener el token de la cabecera HTTP
        // Verificar si el token está presente
        if (StringUtils.isEmpty(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token no proporcionado");
        }
        // Verificar si el token es válido
        boolean isTokenValid = jwtTokenUtil.validate(token);

        if (!isTokenValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
        }
        Optional<Spaceship> found = spaceshipRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Spaceship current = found.get();
        current.setName(spaceship.getName());
        spaceshipRepository.saveAndFlush(current);
        return ResponseEntity.ok(current);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSpaceship(@PathVariable long id) {
        Optional<Spaceship> found = spaceshipRepository.findById(id);
        if (found.isEmpty())
            return ResponseEntity.notFound().build();
        Spaceship current = found.get();
        spaceshipRepository.delete(current);
        return ResponseEntity.ok(current);
    }

}
