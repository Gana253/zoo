package com.java.zoo.constants;

import java.net.URI;

/**
 * Error Constant to be thrown on error
 */
public final class ErrorConstants {
    public static final String PROBLEM_BASE_URL = "https://www.zoo.tech/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/login-already-used");
    public static final String ERR_VALIDATION = "error.validation";

    private ErrorConstants() {
    }
}
