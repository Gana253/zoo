package com.java.zoo.web.controller;

import com.java.zoo.dto.InputRequest;
import com.java.zoo.entity.Animal;
import com.java.zoo.entity.Room;
import com.java.zoo.exception.BadRequestAlertException;
import com.java.zoo.repository.AnimalRepository;
import com.java.zoo.service.ZooService;
import com.java.zoo.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * REST controller for managing animals/room.
 * <p>
 */
@RestController
@RequestMapping("/api/zoo")
public class ZooController {
    private final Logger log = LoggerFactory.getLogger(ZooController.class);

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private ZooService zooService;

    @Autowired
    private AnimalRepository animalRepository;

    /**
     * {@code PUT  /place}  : Place animal in room
     * <p>
     * Places the animal into the room based on the preference. If not it will be placed in the room whichever is available
     *
     * @param inputRequest the animal to be placed.
     * @return the {@link ResponseEntity} with status  {@code 200 (OK)} and with body the updated room, or with status {@code 400 (Bad Request)} if the animal id is null  or for the given id animal is not available.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the animal id is null  or for the given id animal is not available.
     */
    @PutMapping("/place")
    public ResponseEntity<Room> placeAnimal(@Valid @RequestBody InputRequest inputRequest) {
        log.debug("REST request to place animal in room : {}", inputRequest);

        Optional<Animal> animal;
        if (inputRequest.getAnimalId() == null) {
            throw new BadRequestAlertException("Animal id is empty , request cannot be completed", "place animal", "animalidnull");

        }
        animal = animalRepository.findById(inputRequest.getAnimalId());
        if (!animal.isPresent()) {
            throw new BadRequestAlertException("Couldn't find animal with given id , request cannot be completed", "place animal", "animalidwrong");
        } else {
            Room room = zooService.placeAnimal(animal.get());
            return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, "Animal", animal.get().getId().toString()))
                    .body(room);

        }

    }


    /**
     * {@code PUT  /move}  : move animal from one room to another
     * <p>
     * Move the animal from the existing room to the new one.
     * If user passes the roomid to which the animal to be moved, it will be moved accordingly
     * Else based on the preferences animal will be moved to new room
     *
     * @param inputRequest the animal to be moved.
     * @return the {@link ResponseEntity} with status  {@code 200 (OK)} and with body the updated room, or with status {@code 400 (Bad Request)} if the animal id is null  or for the given id animal is not available.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the animal id is null  or for the given id animal is not available.
     */
    @PutMapping("/move")
    public ResponseEntity<Room> moveAnimal(@Valid @RequestBody InputRequest inputRequest) {
        log.debug("REST request to move animal to another room : {}", inputRequest);

        Optional<Animal> animal;
        if (inputRequest.getAnimalId() == null) {
            throw new BadRequestAlertException("Animal id is empty , request cannot be completed", "move animal", "animalidnull");

        }
        animal = animalRepository.findById(inputRequest.getAnimalId());
        if (!animal.isPresent()) {
            throw new BadRequestAlertException("Couldn't find animal with given id , request cannot be completed", "move animal", "animalidwrong");
        } else {
            Room room = zooService.moveAnimal(animal.get(), inputRequest.getToRoomId());

            return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, "Animal", animal.get().getId().toString()))
                    .body(room);
        }

    }

    /**
     * {@code DELETE / delete} : remove animal from the room
     *
     * @param animalId the animal to be removed from the room.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/delete/{animalId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String animalId) {
        log.debug("REST request to delete animal from room: {}", animalId);
        Optional<Animal> animal;
        if (animalId == null) {
            throw new BadRequestAlertException("Animal id is empty , request cannot be completed", "move animal", "animalidnull");

        }
        animal = animalRepository.findById(Long.parseLong(animalId));
        if (!animal.isPresent()) {
            throw new BadRequestAlertException("Couldn't find animal with given id , request cannot be completed", "move animal", "animalidwrong");
        } else {
            zooService.deleteAnimalFromRoom(animal.get());

            return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "animal deleted from room", animalId)).build();

        }
    }
}
