package com.java.zoo.web.controller;


import com.java.zoo.ZooApplication;
import com.java.zoo.entity.Animal;
import com.java.zoo.repository.AnimalRepository;
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
 * Integration tests for the {@link AnimalController} REST controller.
 */
@SpringBootTest(classes = ZooApplication.class)
@AutoConfigureMockMvc
@WithMockUser
public class AnimalControllerIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_LOCATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LOCATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_PREFERENCE = 1L;
    private static final Long UPDATED_PREFERENCE = 2L;

    @Autowired
    private AnimalRepository animalRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAnimalMockMvc;

    private Animal animal;

    /**
     * Create an entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Animal createEntity(EntityManager em) {
        Animal animal = new Animal();
        animal.setTitle(DEFAULT_TITLE);
        animal.setLocated(DEFAULT_LOCATED);
        animal.setType(DEFAULT_TYPE);
        animal.setPreference(DEFAULT_PREFERENCE);
        return animal;
    }


    @BeforeEach
    public void initTest() {
        animal = createEntity(em);
    }

    @Test
    @Transactional
    public void createAnimal() throws Exception {
        int databaseSizeBeforeCreate = animalRepository.findAll().size();
        // Create the Animal
        restAnimalMockMvc.perform(post("/api/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(animal)))
                .andExpect(status().isCreated());

        // Validate the Animal in the database
        List<Animal> animalList = animalRepository.findAll();
        assertThat(animalList).hasSize(databaseSizeBeforeCreate + 1);
        Animal testAnimal = animalList.get(animalList.size() - 1);
        assertThat(testAnimal.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testAnimal.getLocated()).isEqualTo(DEFAULT_LOCATED);
        assertThat(testAnimal.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testAnimal.getPreference()).isEqualTo(DEFAULT_PREFERENCE);
    }

    @Test
    @Transactional
    public void createAnimalWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = animalRepository.findAll().size();

        // Create the Animal with an existing ID
        animal.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAnimalMockMvc.perform(post("/api/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(animal)))
                .andExpect(status().isBadRequest());

        // Validate the Animal in the database
        List<Animal> animalList = animalRepository.findAll();
        assertThat(animalList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllAnimals() throws Exception {
        // Initialize the database
        animalRepository.saveAndFlush(animal);

        // Get all the animalList
        restAnimalMockMvc.perform(get("/api/animals?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(animal.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
                .andExpect(jsonPath("$.[*].located").value(hasItem(DEFAULT_LOCATED.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE)))
                .andExpect(jsonPath("$.[*].preference").value(hasItem(DEFAULT_PREFERENCE.intValue())));
    }

    @Test
    @Transactional
    public void getAnimal() throws Exception {
        // Initialize the database
        animalRepository.saveAndFlush(animal);

        // Get the animal
        restAnimalMockMvc.perform(get("/api/animals/{id}", animal.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(animal.getId().intValue()))
                .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
                .andExpect(jsonPath("$.located").value(DEFAULT_LOCATED.toString()))
                .andExpect(jsonPath("$.type").value(DEFAULT_TYPE))
                .andExpect(jsonPath("$.preference").value(DEFAULT_PREFERENCE.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingAnimal() throws Exception {
        // Get the animal
        restAnimalMockMvc.perform(get("/api/animals/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAnimal() throws Exception {
        // Initialize the database
        animalRepository.saveAndFlush(animal);

        int databaseSizeBeforeUpdate = animalRepository.findAll().size();

        // Update the animal
        Animal updatedAnimal = animalRepository.findById(animal.getId()).get();
        // Disconnect from session so that the updates on updatedAnimal are not directly saved in db
        em.detach(updatedAnimal);
        updatedAnimal.setTitle(UPDATED_TITLE);
        updatedAnimal.setLocated(UPDATED_LOCATED);
        updatedAnimal.setType(UPDATED_TYPE);
        updatedAnimal.setPreference(UPDATED_PREFERENCE);

        restAnimalMockMvc.perform(put("/api/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(updatedAnimal)))
                .andExpect(status().isOk());

        // Validate the Animal in the database
        List<Animal> animalList = animalRepository.findAll();
        assertThat(animalList).hasSize(databaseSizeBeforeUpdate);
        Animal testAnimal = animalList.get(animalList.size() - 1);
        assertThat(testAnimal.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testAnimal.getLocated()).isEqualTo(UPDATED_LOCATED);
        assertThat(testAnimal.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testAnimal.getPreference()).isEqualTo(UPDATED_PREFERENCE);
    }

    @Test
    @Transactional
    public void updateNonExistingAnimal() throws Exception {
        int databaseSizeBeforeUpdate = animalRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAnimalMockMvc.perform(put("/api/animals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(animal)))
                .andExpect(status().isBadRequest());

        // Validate the Animal in the database
        List<Animal> animalList = animalRepository.findAll();
        assertThat(animalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteAnimal() throws Exception {
        // Initialize the database
        animalRepository.saveAndFlush(animal);

        int databaseSizeBeforeDelete = animalRepository.findAll().size();

        // Delete the animal
        restAnimalMockMvc.perform(delete("/api/animals/{id}", animal.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Animal> animalList = animalRepository.findAll();
        assertThat(animalList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
