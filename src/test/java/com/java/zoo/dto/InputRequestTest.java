package com.java.zoo.dto;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;


public class InputRequestTest {

    private final Logger log = LoggerFactory.getLogger(InputRequestTest.class);

    @Test
    public void testInputRequestObject() {
        InputRequest inputRequest = new InputRequest();
        inputRequest.setRoomId(1L);
        inputRequest.setAnimalId(32L);
        assertThat(inputRequest.getAnimalId()).isEqualTo(32L);
        assertThat(inputRequest.getRoomId()).isEqualTo(1L);
        log.info("Input request ->{}", inputRequest.toString());
    }

}
