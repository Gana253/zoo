package com.java.zoo.web.controller;

import com.java.zoo.ZooApplication;
import com.java.zoo.dto.InputRequest;
import com.java.zoo.dto.JwtRequest;
import com.java.zoo.repository.UserRepository;
import com.java.zoo.service.CommandLineService;
import com.java.zoo.web.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ZooController} REST controller.
 */
@SpringBootTest(classes = ZooApplication.class)
@AutoConfigureMockMvc
public class ZooControllerWithSercurityIT {

    @Autowired
    private MockMvc restZooMockMvc;

    @Autowired
    private MockMvc jwtAuthenticateMvcMock;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private CommandLineService commandLineService;


    @Test
    @Transactional
    public void placeAnimal() throws Exception {
        userRepository.deleteAll();
        commandLineService.loadUsers();
        MvcResult resultJwtToken = getMvcResultForAuthentication();
        InputRequest inputRequest = new InputRequest(52L, 1L);
        // Place Animal in the room and expect status 200
        restZooMockMvc.perform(put("/api/animal/place")
                .header("Authorization", "Bearer " + resultJwtToken.getResponse().getContentAsString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.animals.[*].id").value(hasItem(inputRequest.getAnimalId().intValue())))
                .andExpect(jsonPath("$.id").value(inputRequest.getRoomId().intValue()));

    }

    @Test
    @Transactional
    public void placeAnimalWithoutAuthentication() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 1L);
        // Place Animal in the room without authentication and expect status 401
        restZooMockMvc.perform(put("/api/animal/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isUnauthorized());

    }



    private MvcResult getMvcResultForAuthentication() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("admin");
        request.setPassword("admin");


        return jwtAuthenticateMvcMock
                .perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isOk())
                .andReturn();
    }

}
