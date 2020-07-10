package com.java.zoo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * A DTO representing a password change required data - current and new password.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PasswordChangeDTO implements Serializable {
    private static final long serialVersionUID = -3515177803904240620L;
    private String currentPassword;
    private String newPassword;
}
