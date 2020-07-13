package com.java.zoo.web.controller;


import com.java.zoo.ZooApplication;
import com.java.zoo.dto.JwtRequest;
import com.java.zoo.web.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration Test for the {@link JwtAuthenticationController} REST controller.
 */
@AutoConfigureMockMvc
@SpringBootTest(classes = ZooApplication.class)
public class JwtAuthenticationControllerIT {

    @Autowired
    private MockMvc jwtAuthenticateMvcMock;


    @Test
    public void getTokenForUser() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("admin");
        request.setPassword("admin");


        jwtAuthenticateMvcMock
                .perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    public void getTokenForInvalidUser() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setUsername("admin1");
        request.setPassword("admin");


        jwtAuthenticateMvcMock
                .perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Authentication Denied"))
                .andReturn();

    }
}
