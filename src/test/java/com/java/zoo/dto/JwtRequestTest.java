package com.java.zoo.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DTO to accept the input for the rest endpoint for JwtAuthenticationResource
 */

public class JwtRequestTest {
    @Test
    public void testJwtRequestObject() {
        JwtRequest jwtRequest = new JwtRequest();
        jwtRequest.setUsername("admin");
        jwtRequest.setPassword("admin");
        assertThat(jwtRequest.getUsername()).isEqualTo("admin");
        assertThat(jwtRequest.getPassword()).isEqualTo("admin");

    }

}