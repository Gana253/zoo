package com.java.zoo.web.controller;

import com.java.zoo.constants.Constants;
import com.java.zoo.dto.InputRequest;
import com.java.zoo.entity.Animal;
import com.java.zoo.entity.Room;
import com.java.zoo.exception.BadRequestAlertException;
import com.java.zoo.repository.AnimalRepository;
import com.java.zoo.repository.FavoriteRepository;
import com.java.zoo.repository.RoomRepository;
import com.java.zoo.service.ZooService;
import com.java.zoo.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * REST controller for managing animals/room/favorite.
 * <p>
 */
@RestController
@RequestMapping("/api")
public class ZooController {
    private final Logger log = LoggerFactory.getLogger(ZooController.class);
    private final ZooService zooService;
    private final AnimalRepository animalRepository;
    private final RoomRepository roomRepository;
    private final FavoriteRepository favoriteRepository;
    @Value("${spring.application.name}")
    private String applicationName;

    public ZooController(ZooService zooService, RoomRepository roomRepository, AnimalRepository animalRepository, FavoriteRepository favoriteRepository) {
        this.zooService = zooService;
        this.roomRepository = roomRepository;
        this.animalRepository = animalRepository;
        this.favoriteRepository = favoriteRepository;
    }


    /**
     * {@code PUT  /animal/place}  : Place animal in room
     * <p>
     * Places the animal into the room based on the preference.
     *
     * @param inputRequest the animal id /room id to be placed .
     * @return the {@link ResponseEntity} with status  {@code 200 (OK)} and with body the updated animal, or with status {@code 400 (Bad Request)} if the animal/room id is null  or for the given input animal/room id is not available in DB.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the animal/room id is null  or for the given input animal/room id is not available in DB.
     */
    @PutMapping("/animal/place")
    public ResponseEntity<Animal> placeAnimal(@Valid @RequestBody InputRequest inputRequest) {
        log.debug("REST request to place animal in room : {}", inputRequest.getAnimalId());

        String requestType = "Place Animal";
        Animal animal = validateAnimalId(inputRequest.getAnimalId(), requestType);
        Room room = validateRoomId(inputRequest.getRoomId(), requestType);
        if (null != animal.getRoom()) {
            throw new BadRequestAlertException("Animal is placed in room already! Try moving the animal to different room or remove animal from the existing room, request cannot be completed", requestType, "animalPlacedAlready");
        }
        zooService.placeAnimal(animal, room);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, Constants.ANIMAL_ENTITY_NAME, animal.getId().toString()))
                .body(animal);


    }


    /**
     * {@code PUT  /animal/move}  : move animal from one room to another
     * <p>
     * Move the animal from the existing room to the new one.
     * If user passes the valid roomid to which the animal to be moved, it will be moved accordingly
     *
     * @param inputRequest the animal to be moved.
     * @return the {@link ResponseEntity} with status  {@code 200 (OK)} and with body the updated animal, or with status {@code 400 (Bad Request)} if the animal/room id is null  or for the given input animal/room id is not available in DB.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the animal/room id is null  or for the given input animal/room id is not available in DB.
     */
    @PutMapping("/animal/move")
    public ResponseEntity<Animal> moveAnimal(@Valid @RequestBody InputRequest inputRequest) {
        log.debug("REST request to move animal to another room : {}", inputRequest.getAnimalId());

        String requestType = "Move Animal";
        Animal animal = validateAnimalId(inputRequest.getAnimalId(), requestType);
        validateAnimalRoomAvailable(requestType, animal);
        if (animal.getRoom().getId().equals(inputRequest.getRoomId())) {
            throw new BadRequestAlertException("Animal currently placed room and to be moved room are same Please check, request cannot be completed", requestType, "sameroomid");
        }
        Room room = validateRoomId(inputRequest.getRoomId(), requestType);
        zooService.moveAnimal(animal, room);

        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, Constants.ANIMAL_ENTITY_NAME, animal.getId().toString()))
                .body(animal);
    }

    /**
     * {@code DELETE animal / remove} : remove animal from the room
     *
     * @param animalId the animal to be removed from the room.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the animalid is not available in DB.
     */
    @DeleteMapping("/animal/remove/{animalId}")
    public ResponseEntity<Void> removeAnimal(@PathVariable String animalId) {
        log.debug("REST request to delete animal from room: {}", animalId);
        String requestType = "Delete Animal";
        Animal animal = validateAnimalId(Long.parseLong(animalId), requestType);
        validateAnimalRoomAvailable(requestType, animal);
        zooService.deleteAnimalFromRoom(animal);

        return ResponseEntity.noContent().headers(HeaderUtil.createAlert(applicationName, "animal deleted from room", animalId)).build();


    }

    /**
     * {@code POST  /favorite/assign}  : assign room as favorite for animal
     * <p>
     * Assign the room as favorite for given animal
     *
     * @param inputRequest the animal & room to be assigned as favorite.
     * @return the {@link ResponseEntity} with status  {@code 201 (Created)} and with body the updated animal, or with status {@code 400 (Bad Request)} if the animal/room id is null  or for the given input animal/room id is not available in DB.
     * @throws BadRequestAlertException {@code 400 (Bad Request)} if the animal/room id is null  or for the given input animal/room id is not available in DB.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/favorite/assign")
    public ResponseEntity<Animal> assignFavorite(@Valid @RequestBody InputRequest inputRequest) throws URISyntaxException {
        log.debug("REST request to assign room as favorite for  animal: {}", inputRequest.getAnimalId());

        String requestType = "Assign Favorite Room";
        Animal animal = validateAnimalId(inputRequest.getAnimalId(), requestType);
        Room room = validateRoomId(inputRequest.getRoomId(), requestType);
        if (null != favoriteRepository.findByRoomIdAndAnimalId(room.getId(), animal.getId())) {
            throw new BadRequestAlertException("Room is already assigned as favorite for the animal! Try with another room id, request cannot be completed", requestType, "roomidassignedalready");
        }
        zooService.assignFavoriteRoom(animal, room);
        return ResponseEntity.created(new URI("api//favorite/assign/" + animal.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, Constants.ANIMAL_ENTITY_NAME, animal.getId().toString()))
                .body(animal);
    }

    /**
     * {@code DeleteMapping  /favorite/unassign}  : unassign room as favorite for animal
     * <p>
     * UnAssign the room as favorite for given animal
     *
     * @param inputRequest the animal & room to be assigned as favorite.
     * @return the {@link ResponseEntity} with status  {@code 200 (OK)} and with body the updated animal, or with status {@code 400 (Bad Request)} if the animal/room id is null  or for the given input animal/room id is not available in DB.
     * @throws BadRequestAlertException {@code 400 (Bad Request)}if the animal/room id is null  or for the given input animal/room id is not available in DB.
     */
    @DeleteMapping("/favorite/unassign")
    public ResponseEntity<Animal> unassignFavorite(@Valid @RequestBody InputRequest inputRequest) {
        log.debug("REST request to unassign room as favorite for  animal: {}", inputRequest.getAnimalId());

        String requestType = "UnAssign Favorite Room";
        Animal animal = validateAnimalId(inputRequest.getAnimalId(), requestType);
        Room room = validateRoomId(inputRequest.getRoomId(), requestType);
        zooService.unassignFavoriteRoom(animal, room);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, Constants.ANIMAL_ENTITY_NAME, animal.getId().toString()))
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

    private void validateAnimalRoomAvailable(String requestType, Animal animal) {
        if (null == animal.getRoom()) {
            throw new BadRequestAlertException("Animal is not associated with any room try placing the animal to a room, request cannot be completed", requestType, "notassociatedwithroom");
        }
    }
}
