package com.java.zoo.exception;


import com.java.zoo.constants.ErrorConstants;

public class InvalidPasswordException extends BadRequestAlertException {
    private static final long serialVersionUID = 1L;

    public InvalidPasswordException() {
        super(ErrorConstants.LOGIN_ALREADY_USED_TYPE, "Incorrect Password!", "userManagement", "passwordincorrect");
    }
}
