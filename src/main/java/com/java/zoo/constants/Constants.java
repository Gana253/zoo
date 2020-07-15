package com.java.zoo.constants;

import lombok.NoArgsConstructor;

/**
 * Generic constants file for the application
 */
@NoArgsConstructor
public final class Constants {
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";
    public static final String ANONYMOUS_USER = "anonymoususer";
    public static final String CREATED_BY = "SYSTEM";
    public static final String ANIMAL_ENTITY_NAME = "animal";
}
