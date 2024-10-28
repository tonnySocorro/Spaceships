package com.spaceships;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

import com.spaceships.models.Spaceship;
import com.spaceships.repositories.SpaceshipRepository;

@SpringBootTest
@AutoConfigureMockMvc
public class SpaceshipsIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpaceshipRepository spaceshipRepository;

    @Test
    public void testGetSpaceshipById() throws Exception {
        Spaceship spaceship = new Spaceship(1L, "X-wing");
        spaceshipRepository.saveAndFlush(spaceship);
        when(spaceshipRepository.findById(1L)).thenReturn(Optional.of(spaceship));
        mockMvc.perform(get("/api/spaceships/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("{\"id\":1,\"name\":\"X-wing\"}"));
    }

}
