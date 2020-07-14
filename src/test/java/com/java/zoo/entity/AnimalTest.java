package com.java.zoo.entity;


import com.java.zoo.web.util.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnimalTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Animal.class);
        Animal animal1 = new Animal();
        animal1.setId(1L);
        Animal animal2 = new Animal();
        animal2.setId(animal1.getId());
        assertThat(animal1).isEqualTo(animal2);
        animal2.setId(2L);
        assertThat(animal1).isNotEqualTo(animal2);
        animal1.setId(null);
        assertThat(animal1).isNotEqualTo(animal2);
    }
}
