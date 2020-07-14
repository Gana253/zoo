package com.java.zoo.entity;

import com.java.zoo.web.util.TestUtil;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class RoomTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Room.class);
        Room room1 = new Room();
        room1.setId(1L);
        room1.setCreated(Instant.now());
        Room room2 = new Room();
        room2.setId(room1.getId());
        room2.setCreated(room1.getCreated());
        assertThat(room1).isEqualTo(room2);
        room2.setId(2L);
        assertThat(room1).isNotEqualTo(room2);
        room1.setId(null);
        assertThat(room1).isNotEqualTo(room2);
    }
}
