package com.java.zoo.dto;


import org.junit.jupiter.api.Test;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class AnimalsJsonObjectTest {
    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();

    @Test
    public void testAnimalProjection() {

        AnimalsJsonObject animalsJsonObject = factory.createProjection(AnimalsJsonObject.class);
        animalsJsonObject.setTitle("CAT");
        Instant located = Instant.now();
        animalsJsonObject.setLocated(located);
        assertThat(animalsJsonObject.getTitle()).isEqualTo("CAT");
        assertThat(animalsJsonObject.getLocated()).isEqualTo(located);
    }

}
