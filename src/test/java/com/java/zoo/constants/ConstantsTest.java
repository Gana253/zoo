package com.java.zoo.constants;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ConstantsTest {

    @Test
    public void testConstants() {
        assertThat(Constants.CREATED_BY).isEqualTo("SYSTEM");
        assertThat(Constants.ANIMAL_ENTITY_NAME).isEqualTo("animal");
        assertThat(Constants.ANONYMOUS_USER).isEqualTo("anonymoususer");
        assertThat(Constants.LOGIN_REGEX).isEqualTo("^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$");
    }

}
