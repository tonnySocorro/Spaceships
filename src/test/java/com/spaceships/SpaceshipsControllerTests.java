package com.spaceships;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;


import com.spaceships.models.Spaceship;
import com.spaceships.repositories.SpaceshipRepository;

import org.springframework.data.domain.Page;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
@AutoConfigureMockMvc
public class SpaceshipsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpaceshipRepository spaceshipRepository;

    @Test
    public void testGetSpaceships() throws Exception {
        Spaceship spaceship1 = new Spaceship(1L, "X-wing");
        Spaceship spaceship2 = new Spaceship(2L, "Millennium Falcon");
        spaceshipRepository.saveAndFlush(spaceship1);
        spaceshipRepository.saveAndFlush(spaceship2);
        Page<Spaceship> page = new PageImpl<>(Arrays.asList(spaceship1, spaceship2));

        when(spaceshipRepository.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/spaceships")
                .param("page", "0") // Número de página
                .param("size", "2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("X-wing"))
                .andExpect(jsonPath("$.content[1].name").value("Millennium Falcon"));
    }

    @Test
    public void testDeleteSpaceship() throws Exception {
        Spaceship spaceship = new Spaceship(10L, "X-wing");

        when(spaceshipRepository.findById(1L)).thenReturn(Optional.of(spaceship));
        doNothing().when(spaceshipRepository).delete(spaceship);

        mockMvc.perform(delete("/api/spaceships/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteSpaceship_NotFound() throws Exception {
        when(spaceshipRepository.findById(10L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/spaceships/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
