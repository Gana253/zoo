package com.java.zoo.constants;

import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Error Constant to be thrown on error
 */
public final class ErrorConstantsTest {
    @Test
    public void testErrorConstants() {
        assertThat(ErrorConstants.PROBLEM_BASE_URL).isEqualTo("https://www.zoo.tech/problem");
        assertThat(ErrorConstants.DEFAULT_TYPE).isEqualTo(URI.create(ErrorConstants.PROBLEM_BASE_URL + "/problem-with-message"));
        assertThat(ErrorConstants.EMAIL_ALREADY_USED_TYPE).isEqualTo(URI.create(ErrorConstants.PROBLEM_BASE_URL + "/email-already-used"));
        assertThat(ErrorConstants.LOGIN_ALREADY_USED_TYPE).isEqualTo(URI.create(ErrorConstants.PROBLEM_BASE_URL + "/login-already-used"));
        assertThat(ErrorConstants.ERR_VALIDATION).isEqualTo("error.validation");
    }
}
