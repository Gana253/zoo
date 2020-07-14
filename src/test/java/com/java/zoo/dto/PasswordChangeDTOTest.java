package com.java.zoo.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * A DTO representing a password change required data - current and new password.
 */
public class PasswordChangeDTOTest {
    @Test
    public void testPasswordChangeDTOTest() {
        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO();
        passwordChangeDTO.setCurrentPassword("admin");
        passwordChangeDTO.setNewPassword("admin1");
        assertThat(passwordChangeDTO.getCurrentPassword()).isEqualTo("admin");
        assertThat(passwordChangeDTO.getNewPassword()).isEqualTo("admin1");

    }
}
