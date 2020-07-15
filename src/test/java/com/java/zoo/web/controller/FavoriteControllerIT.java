package com.java.zoo.web.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.java.zoo.ZooApplication;
import com.java.zoo.dto.InputRequest;
import com.java.zoo.entity.Favorite;
import com.java.zoo.repository.FavoriteRepository;
import com.java.zoo.web.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link FavoriteController} REST controller.
 */
@SpringBootTest(classes = ZooApplication.class)
@AutoConfigureMockMvc
@WithMockUser
public class FavoriteControllerIT {

    private static final Long DEFAULT_ROOM_ID = 1L;
    private static final Long UPDATED_ROOM_ID = 2L;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFavoriteMockMvc;

    @Autowired
    private MockMvc restZooMockMvc;

    private Favorite favorite;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Favorite createEntity(EntityManager em) {
        Favorite favorite = new Favorite();
        favorite.setRoomId(DEFAULT_ROOM_ID);
        return favorite;
    }

    @BeforeEach
    public void initTest() {
        favorite = createEntity(em);
    }

    @Test
    @Transactional
    public void createFavorite() throws Exception {
        int databaseSizeBeforeCreate = favoriteRepository.findAll().size();
        // Create the Favorite
        restFavoriteMockMvc.perform(post("/api/favorites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
                .andExpect(status().isCreated());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeCreate + 1);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getRoomId()).isEqualTo(DEFAULT_ROOM_ID);
    }

    @Test
    @Transactional
    public void createFavoriteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = favoriteRepository.findAll().size();

        // Create the Favorite with an existing ID
        favorite.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restFavoriteMockMvc.perform(post("/api/favorites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
                .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllFavorites() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favoriteList
        restFavoriteMockMvc.perform(get("/api/favorites?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())))
                .andExpect(jsonPath("$.[*].roomId").value(hasItem(DEFAULT_ROOM_ID.intValue())));
    }

    @Test
    @Transactional
    public void getFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get the favorite
        restFavoriteMockMvc.perform(get("/api/favorites/{id}", favorite.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(favorite.getId().intValue()))
                .andExpect(jsonPath("$.roomId").value(DEFAULT_ROOM_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingFavorite() throws Exception {
        // Get the favorite
        restFavoriteMockMvc.perform(get("/api/favorites/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // Update the favorite
        Favorite updatedFavorite = favoriteRepository.findById(favorite.getId()).get();
        // Disconnect from session so that the updates on updatedFavorite are not directly saved in db
        em.detach(updatedFavorite);
        updatedFavorite.setRoomId(UPDATED_ROOM_ID);

        restFavoriteMockMvc.perform(put("/api/favorites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(updatedFavorite)))
                .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favoriteList.get(favoriteList.size() - 1);
        assertThat(testFavorite.getRoomId()).isEqualTo(UPDATED_ROOM_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingFavorite() throws Exception {
        int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFavoriteMockMvc.perform(put("/api/favorites")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
                .andExpect(status().isBadRequest());

        // Validate the Favorite in the database
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        int databaseSizeBeforeDelete = favoriteRepository.findAll().size();

        // Delete the favorite
        restFavoriteMockMvc.perform(delete("/api/favorites/{id}", favorite.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Favorite> favoriteList = favoriteRepository.findAll();
        assertThat(favoriteList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void getAllFavoriteRoomId() throws Exception {
        InputRequest inputRequest = new InputRequest(54L, 2L);
        // assign room as favorite for the given animal and expect status 200
        restZooMockMvc.perform(post("/api/favorite/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest)))
                .andExpect(status().isOk());

        InputRequest inputRequest1 = new InputRequest(54L, 1L);
        // assign room as favorite for the given animal and expect status 200
        restZooMockMvc.perform(post("/api/favorite/assign")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(inputRequest1)))
                .andExpect(status().isOk());
        // Get the favorite
        MvcResult result = restFavoriteMockMvc.perform(get("/api/favorites/room/54")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = TestUtil.mapper.readTree(result.getResponse().getContentAsString());
        assertThat(2).isEqualTo(node.size());
    }
}
