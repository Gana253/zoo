package com.java.zoo.web.controller;

import com.java.zoo.ZooApplication;
import com.java.zoo.entity.Room;
import com.java.zoo.repository.RoomRepository;
import com.java.zoo.web.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link RoomController} REST controller.
 */
@SpringBootTest(classes = ZooApplication.class)
@AutoConfigureMockMvc
@WithMockUser
public class RoomControllerIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Long DEFAULT_SIZE = 0L;
    private static final Long UPDATED_SIZE = 1L;

    private static final Instant DEFAULT_CREATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRoomMockMvc;

    private Room room;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Room createEntity(EntityManager em) {
        Room room = new Room();
        room.setTitle(DEFAULT_TITLE);
        room.setSize(DEFAULT_SIZE);
        room.setCreated(DEFAULT_CREATED);
        return room;
    }


    @BeforeEach
    public void initTest() {
        room = createEntity(em);
    }

    @Test
    @Transactional
    public void createRoom() throws Exception {

        int databaseSizeBeforeCreate = roomRepository.findAll().size();
        room.setTitle("EEEEEEE");
        // Create the Room
        restRoomMockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(room)))
                .andExpect(status().isCreated());

        // Validate the Room in the database
        List<Room> roomList = roomRepository.findAll();
        assertThat(roomList).hasSize(databaseSizeBeforeCreate + 1);
    }

    @Test
    @Transactional
    public void createRoomWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = roomRepository.findAll().size();

        // Create the Room with an existing ID
        room.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRoomMockMvc.perform(post("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(room)))
                .andExpect(status().isBadRequest());

        // Validate the Room in the database
        List<Room> roomList = roomRepository.findAll();
        assertThat(roomList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllRooms() throws Exception {
        // Initialize the database
        roomRepository.saveAndFlush(room);

        // Get all the roomList
        restRoomMockMvc.perform(get("/api/rooms?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(room.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
                .andExpect(jsonPath("$.[*].size").value(hasItem(DEFAULT_SIZE.intValue())))
                .andExpect(jsonPath("$.[*].created").value(hasItem(DEFAULT_CREATED.toString())));
    }

    @Test
    @Transactional
    public void getRoom() throws Exception {
        // Initialize the database
        room.setTitle("GGGGGGG");
        roomRepository.saveAndFlush(room);

        // Get the room
        restRoomMockMvc.perform(get("/api/rooms/{id}", room.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(room.getId().intValue()))
                .andExpect(jsonPath("$.title").value("GGGGGGG"))
                .andExpect(jsonPath("$.size").value(DEFAULT_SIZE.intValue()))
                .andExpect(jsonPath("$.created").value(DEFAULT_CREATED.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRoom() throws Exception {
        // Get the room
        restRoomMockMvc.perform(get("/api/rooms/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRoom() throws Exception {
        // Initialize the database
        room.setTitle("CCCCCCC");
        roomRepository.saveAndFlush(room);

        int databaseSizeBeforeUpdate = roomRepository.findAll().size();

        // Update the room
        Room updatedRoom = roomRepository.findById(room.getId()).get();
        // Disconnect from session so that the updates on updatedRoom are not directly saved in db
        em.detach(updatedRoom);
        updatedRoom.setTitle("DDDDDDD");
        updatedRoom.setSize(UPDATED_SIZE);
        updatedRoom.setCreated(UPDATED_CREATED);

        restRoomMockMvc.perform(put("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(updatedRoom)))
                .andExpect(status().isOk());

        // Validate the Room in the database
        List<Room> roomList = roomRepository.findAll();
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate);
        Room testRoom = roomList.get(roomList.size() - 1);
        assertThat(testRoom.getTitle()).isEqualTo("DDDDDDD");
        assertThat(testRoom.getSize()).isEqualTo(UPDATED_SIZE);
        assertThat(testRoom.getCreated()).isEqualTo(UPDATED_CREATED);
    }

    @Test
    @Transactional
    public void updateNonExistingRoom() throws Exception {
        int databaseSizeBeforeUpdate = roomRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRoomMockMvc.perform(put("/api/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(room)))
                .andExpect(status().isBadRequest());

        // Validate the Room in the database
        List<Room> roomList = roomRepository.findAll();
        assertThat(roomList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRoom() throws Exception {
        // Initialize the database
        room.setTitle("JJJJJJJ");
        roomRepository.saveAndFlush(room);

        int databaseSizeBeforeDelete = roomRepository.findAll().size();

        // Delete the room
        restRoomMockMvc.perform(delete("/api/rooms/{id}", room.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Room> roomList = roomRepository.findAll();
        assertThat(roomList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
