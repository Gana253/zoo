package com.java.zoo.web.controller;

import com.java.zoo.ZooApplication;
import com.java.zoo.dto.InputRequest;
import com.java.zoo.entity.Animal;
import com.java.zoo.repository.AnimalRepository;
import com.java.zoo.web.util.TestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link ZooController} REST controller.
 */
@SpringBootTest(classes = ZooApplication.class)
@AutoConfigureMockMvc
@WithMockUser
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ZooControllerIT {

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private MockMvc restZooMockMvc;

    @Test
    @Transactional
    @Order(1)
    public void placeAnimal() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 1L);
        // Place Animal in the room and expect status 200
        restZooMockMvc.perform(post("/api/animal/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.room.id").value(inputRequest.getRoomId().intValue()))
                .andExpect(jsonPath("$.id").value(inputRequest.getAnimalId().intValue()));

    }

    @Test
    @Transactional
    @Order(2)
    public void placeAnimalInRoomAgain() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 2L);
        // Place Animal in the room and expect status 200
        restZooMockMvc.perform(post("/api/animal/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    public void placeAnimalWithInvalidAnimalId() throws Exception {
        InputRequest inputRequest = new InputRequest();
        inputRequest.setAnimalId(0L);
        inputRequest.setRoomId(1L);
        // Place Animal in the room with invalid animal id  and expect status 404
        restZooMockMvc.perform(post("/api/animal/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void placeAnimalWithInvalidRoomId() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 0L);
        // Place Animal in the room with invalid room id  and expect status 404
        restZooMockMvc.perform(post("/api/animal/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @Transactional
    @Order(3)
    public void moveAnimal() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 2L);
        // Move Animal from existing room to the other and expect status 200
        restZooMockMvc.perform(post("/api/animal/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.room.id").value(inputRequest.getRoomId().intValue()))
                .andExpect(jsonPath("$.id").value(inputRequest.getAnimalId().intValue()));
    }

    @Test
    @Transactional
    public void moveAnimalWithInvalidAnimalId() throws Exception {
        InputRequest inputRequest = new InputRequest(0L, 1L);
        // Move Animal in the room with invalid animal id  and expect status 404
        restZooMockMvc.perform(post("/api/animal/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void moveAnimalWithInvalidRoomId() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 0L);
        // Move Animal in the room with invalid room id  and expect status 404
        restZooMockMvc.perform(post("/api/animal/move")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @Order(4)
    public void placeAnotherAnimal() throws Exception {
        InputRequest inputRequest = new InputRequest(53L, 2L);
        // Place Animal in the room and expect status 200
        restZooMockMvc.perform(post("/api/animal/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.room.id").value(inputRequest.getRoomId().intValue()))
                .andExpect(jsonPath("$.id").value(inputRequest.getAnimalId().intValue()));

    }

    @Test
    @Transactional
    @Order(5)
    public void removeAnimalFromRoom() throws Exception {
        Optional<Animal> animal = animalRepository.findById(53L);

        assertThat(animal.get().getRoom()).isNotNull();
        // delete Animal from the room associated and expect status 204
        restZooMockMvc.perform(delete("/api/animal/remove/{id}", 53)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                Optional<Animal> updatedAnimal = animalRepository.findById(53L);

                assertThat(updatedAnimal.get().getRoom()).isNull();
            }
        });
    }

    @Test
    @Transactional
    public void removeAnimalFromRoomWithInvalidId() throws Exception {
        // throw bad request exception 404
        restZooMockMvc.perform(delete("/api/animal/remove/{id}", 0)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @Order(6)
    public void assignFavorite() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 3L);
        // assign room as favorite for the given animal and expect status 200
        restZooMockMvc.perform(post("/api/favorite/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(inputRequest.getAnimalId().intValue()))
                .andExpect(jsonPath("$.favorites.[*].roomId").value(hasItem(inputRequest.getRoomId().intValue())));
    }

    @Test
    @Transactional
    public void assignFavoriteWithInvalidAnimalId() throws Exception {
        InputRequest inputRequest = new InputRequest(0L, 1L);
        // assign room as favorite with invalid animal id  and expect status 404
        restZooMockMvc.perform(post("/api/favorite/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void assignFavoriteWithInvalidRoomId() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 0L);
        // assign room as favorite with invalid room id  and expect status 404
        restZooMockMvc.perform(post("/api/favorite/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @Order(7)
    public void assignFavoriteSameRoomAgainForAnimal() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 3L);
        // assign room as favorite for the given animal and expect status 200
        restZooMockMvc.perform(post("/api/favorite/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @Order(8)
    public void unAssignFavorite() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 3L);
        // unassign room as favorite for the given animal and expect status 200
        restZooMockMvc.perform(delete("/api/favorite/unassign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(inputRequest.getAnimalId().intValue()));
    }

    @Test
    @Transactional
    public void unAssignFavoriteWithInvalidAnimalId() throws Exception {
        InputRequest inputRequest = new InputRequest(0L, 1L);
        // unassign room as favorite with invalid animal id  and expect status 404
        restZooMockMvc.perform(delete("/api/favorite/unassign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    public void unAssignFavoriteWithInvalidRoomId() throws Exception {
        InputRequest inputRequest = new InputRequest(52L, 0L);
        // unassign room as favorite with invalid room id  and expect status 404
        restZooMockMvc.perform(delete("/api/favorite/unassign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isBadRequest());
    }

}
