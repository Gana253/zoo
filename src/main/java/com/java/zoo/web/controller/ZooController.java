package com.java.zoo.web.controller;

import com.java.zoo.dto.InputRequest;
import com.java.zoo.entity.Animal;
import com.java.zoo.entity.Room;
import com.java.zoo.exception.BadRequestAlertException;
import com.java.zoo.repository.AnimalRepository;
import com.java.zoo.repository.RoomRepository;
import com.java.zoo.service.ZooService;
import com.java.zoo.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * REST controller for managing animals/room/favorite.
 * <p>
 */
@RestController
@RequestMapping("/api")
public class ZooController {
    private final Logger log = LoggerFactory.getLogger(ZooController.class);

    @Value("${spring.application.name}")
    private String applicationName;

    private final ZooService zooService;


    private final AnimalRepository animalRepository;


    private final RoomRepository roomRepository;

    public ZooController(ZooService zooService, RoomRepository roomRepository, AnimalRepository animalRepository) {
        this.zooService = zooService;
        this.roomRepository = roomRepository;
        this.animalRepository = animalRepository;
    }


    /**
     * {@code PUT  /animal/place}  : Place animal in room
     * <p>
     * Places the animal into the room based on the preference. If not it will be placed in the room whichever is available
     *
     * @param inputRequest the animal id /room id to be placed .
     * @return the {@link ResponseEntity} with status  {@code 200 (OK)} and with body the updated room, or with status {@code 400 (Bad Request)} if the animal id is null  or for the given id animal is not available.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the animal id is null  or for the given id animal is not available.
     */
    @PutMapping("/animal/place")
    public ResponseEntity<Room> placeAnimal(@Valid @RequestBody InputRequest inputRequest) {
        log.debug("REST request to place animal in room : {}", inputRequest.toString());

        String requestType = "Place Animal";
        Animal animal = validateAnimalId(inputRequest.getAnimalId(), requestType);
        Room room = validateRoomId(inputRequest.getToRoomId(), requestType);
        zooService.placeAnimal(animal, room);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, "Animal", animal.getId().toString()))
                .body(room);


    }


    /**
     * {@code PUT  /animal/move}  : move animal from one room to another
     * <p>
     * Move the animal from the existing room to the new one.
     * If user passes the roomid to which the animal to be moved, it will be moved accordingly
     * Else based on the preferences animal will be moved to new room
     *
     * @param inputRequest the animal to be moved.
     * @return the {@link ResponseEntity} with status  {@code 200 (OK)} and with body the updated room, or with status {@code 400 (Bad Request)} if the animal id is null  or for the given id animal is not available.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the animal id is null  or for the given id animal is not available.
     */
    @PutMapping("/animal/move")
    public ResponseEntity<Room> moveAnimal(@Valid @RequestBody InputRequest inputRequest) {
        log.debug("REST request to move animal to another room : {}", inputRequest.toString());

        String requestType = "Move Animal";
        Animal animal = validateAnimalId(inputRequest.getAnimalId(), requestType);
        Room room = validateRoomId(inputRequest.getToRoomId(), requestType);
        zooService.moveAnimal(animal, room);

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, "Animal", animal.getId().toString()))
                .body(room);


    }

    /**
     * {@code DELETE animal / delete} : remove animal from the room
     *
     * @param animalId the animal to be removed from the room.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/animal/delete/{animalId}")
    public ResponseEntity<Void> deleteAnimal(@PathVariable String animalId) {
        log.debug("REST request to delete animal from room: {}", animalId);
        String requestType = "Delete Animal";
        Animal animal = validateAnimalId(Long.parseLong(animalId), requestType);
        zooService.deleteAnimalFromRoom(animal);

        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "animal deleted from room", animalId)).build();


    }

    /**
     * {@code PUT  /favorite/assign}  : assign room as favorite for animal
     * <p>
     * Assign the room as favorite for given animal
     *
     * @param inputRequest the animal & room to be assigned as favorite.
     * @return the {@link ResponseEntity} with status  {@code 200 (OK)} and with body the updated animal, or with status {@code 400 (Bad Request)} if the animal id/room id is null  or for the given id animal/room is not available.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the animal id/room id is null  or for the given id animal/room is not available.
     */
    @PutMapping("/favorite/assign")
    public ResponseEntity<Animal> assignFavorite(@Valid @RequestBody InputRequest inputRequest) {
        log.debug("REST request to assign room as favorite for  animal: {}", inputRequest.toString());

        String requestType = "Assign Favorite Room";
        Animal animal = validateAnimalId(inputRequest.getAnimalId(), requestType);
        Room room = validateRoomId(inputRequest.getToRoomId(), requestType);
        zooService.assignFavoriteRoom(animal, room);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, "Animal", animal.getId().toString()))
                .body(animal);


    }

    /**
     * {@code PUT  /favorite/assign}  : unassign room as favorite for animal
     * <p>
     * UnAssign the room as favorite for given animal
     *
     * @param inputRequest the animal & room to be assigned as favorite.
     * @return the {@link ResponseEntity} with status  {@code 200 (OK)} and with body the updated animal, or with status {@code 400 (Bad Request)} if the animal id/room id is null  or for the given id animal/room is not available.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the animal id/room id is null  or for the given id animal/room is not available.
     */
    @PutMapping("/favorite/unassign")
    public ResponseEntity<Animal> unassignFavorite(@Valid @RequestBody InputRequest inputRequest) {
        log.debug("REST request to unassign room as favorite for  animal: {}", inputRequest.toString());

        String requestType = "UnAssign Favorite Room";
        Animal animal = validateAnimalId(inputRequest.getAnimalId(), requestType);
        Room room = validateRoomId(inputRequest.getToRoomId(), requestType);
        zooService.unassignFavoriteRoom(animal, room);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, "Animal", animal.getId().toString()))
                .body(animal);
    }

    private Room validateRoomId(Long roomId, String requestType) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (!room.isPresent()) {
            throw new BadRequestAlertException("Couldn't find room with given id , request cannot be completed", requestType, "roomidwrong");
        }
        return room.get();
    }

    private Animal validateAnimalId(Long animalId, String requestType) {
        Optional<Animal> animal = animalRepository.findById(animalId);

        if (!animal.isPresent()) {
            throw new BadRequestAlertException("Couldn't find animal with given id , request cannot be completed", requestType, "animalidwrong");
        }
        return animal.get();
    }
}
