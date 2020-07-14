package com.java.zoo.dto;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A DTO representing a user, with his authorities.
 */
public class UserDTOTest {

    @Test
    public void testUserDTO() {
        String defaultString = "AAAAAA";
        UserDTO userDTO = new UserDTO();
        userDTO.setCreatedBy(defaultString);
        userDTO.setEmail(defaultString);
        userDTO.setFirstName(defaultString);
        userDTO.setLangKey(defaultString);
        userDTO.setLastName(defaultString);
        userDTO.setLogin(defaultString);
        userDTO.setPassword(defaultString);
        userDTO.setId(1L);

        assertThat(userDTO.getId()).isEqualTo(1L);
        assertThat(userDTO.getEmail()).isEqualTo(defaultString);
        assertThat(userDTO.getCreatedBy()).isEqualTo(defaultString);
        assertThat(userDTO.getFirstName()).isEqualTo(defaultString);
        assertThat(userDTO.getLangKey()).isEqualTo(defaultString);
        assertThat(userDTO.getLastName()).isEqualTo(defaultString);
        assertThat(userDTO.getLogin()).isEqualTo(defaultString);
        assertThat(userDTO.getPassword()).isEqualTo(defaultString);


    }
}
