package com.java.zoo.web.controller;


import com.java.zoo.dto.AnimalsJsonObject;
import com.java.zoo.dto.HappyAnimalsJsonObject;
import com.java.zoo.entity.Animal;
import com.java.zoo.exception.BadRequestAlertException;
import com.java.zoo.repository.AnimalRepository;
import com.java.zoo.util.HeaderUtil;
import com.java.zoo.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link Animal}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AnimalController {

    private final Logger log = LoggerFactory.getLogger(AnimalController.class);

    private static final String ENTITY_NAME = "animal";

    @Value("${spring.application.name}")
    private String applicationName;

    private final AnimalRepository animalRepository;

    public AnimalController(AnimalRepository animalRepository) {
        this.animalRepository = animalRepository;
    }

    /**
     * {@code POST  /animals} : Create a new animal.
     *
     * @param animal the animal to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new animal, or with status {@code 400 (Bad Request)} if the animal has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/animals")
    public ResponseEntity<Animal> createAnimal(@RequestBody Animal animal) throws URISyntaxException {
        log.debug("REST request to save Animal : {}", animal);
        if (animal.getId() != null) {
            throw new BadRequestAlertException("A new animal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Animal result = animalRepository.save(animal);
        return ResponseEntity.created(new URI("/api/animals/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * {@code PUT  /animals} : Updates an existing animal.
     *
     * @param animal the animal to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated animal,
     * or with status {@code 400 (Bad Request)} if the animal is not valid,
     * or with status {@code 500 (Internal Server Error)} if the animal couldn't be updated.
     */
    @PutMapping("/animals")
    public ResponseEntity<Animal> updateAnimal(@RequestBody Animal animal) {
        log.debug("REST request to update Animal : {}", animal);
        if (animal.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Animal result = animalRepository.save(animal);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, animal.getId().toString()))
                .body(result);
    }

    /**
     * {@code GET  /animals} : get all the animals.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of animals in body.
     */
    @GetMapping("/animals")
    public List<Animal> getAllAnimals() {
        log.debug("REST request to get all Animals");
        return animalRepository.findAll();
    }

    /**
     * {@code GET  /animals/:id} : get the "id" animal.
     *
     * @param id the id of the animal to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the animal, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/animals/{id}")
    public ResponseEntity<Animal> getAnimal(@PathVariable Long id) {
        log.debug("REST request to get Animal : {}", id);
        Optional<Animal> animal = animalRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(animal);
    }

    /**
     * {@code DELETE  /animals/:id} : delete the "id" animal.
     *
     * @param id the id of the animal to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/animals/{id}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable Long id) {
        log.debug("REST request to delete Animal : {}", id);
        animalRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code GET  /animals/withoutroom} : get the List of animals which is not assigned with room.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the list of animals.
     */
    @GetMapping("/animals/withoutroom")
    public List<AnimalsJsonObject> getAnimalsWithOutRoom() {
        log.debug("REST request to get Animals without room ");
        return animalRepository.findAllByRoomIsNullOrderByLocatedDesc();
    }


    /**
     * {@code GET  animals/room/{roomId}} : get all animals in the room.
     *
     * @param roomId the id of the room.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Animals in body.
     */
    @GetMapping("/animals/room/{roomId}")
    public List<AnimalsJsonObject> getAllAnimalsInRoom(@PathVariable Long roomId) {
        log.debug("REST request to get all animals in the Room: {}",roomId);
        return animalRepository.findAllByRoom_IdEqualsOrderByLocatedDesc(roomId);
    }

    /**
     * {@code GET  /animals/happyanimals} : get all Happy animals in the rooms.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of happy animals in the rooms in body.
     */
    @GetMapping("/animals/happyanimals")
    public List<HappyAnimalsJsonObject> getAllHappyAnimalsInRoom() {
        log.debug("REST request to get all happy animals in the Rooms");
        return animalRepository.findAllHappyAnimals();
    }


}
