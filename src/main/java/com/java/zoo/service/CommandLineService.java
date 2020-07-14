package com.java.zoo.service;


import com.java.zoo.constants.Constants;
import com.java.zoo.dto.UserDTO;
import com.java.zoo.entity.Animal;
import com.java.zoo.entity.Room;
import com.java.zoo.repository.AnimalRepository;
import com.java.zoo.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

/**
 * Serivce class for the application. To load data.
 */
@Service
public class CommandLineService {

    public static final String COMMA = ";";
    public static final String ERROR_OCCURRED = "Error Occurred -> {}";
    private static final Logger log = LoggerFactory.getLogger(CommandLineService.class);
    private final ResourceLoader resourceLoader;


    private final UserService userService;


    private final RoomRepository roomRepository;


    private final AnimalRepository animalRepository;

    @Value("${inputfile.user-name}")
    private String userFilePath;

    @Value("${inputfile.room-name}")
    private String roomFilePath;

    @Value("${inputfile.animal-name}")
    private String animalFilePath;


    public CommandLineService(ResourceLoader resourceLoader, UserService userService, RoomRepository roomRepository, AnimalRepository animalRepository) {
        this.resourceLoader = resourceLoader;
        this.userService = userService;
        this.roomRepository = roomRepository;
        this.animalRepository = animalRepository;
    }

    /**
     * Load User data on startup
     *
     * @throws IOException when CSV file is not found
     */

    public void loadUsers() throws IOException {
        //Load User from the user.csv file and persist in table
        saveUserData();

    }

    public void saveAnimalData() throws IOException {
        Resource animalResource = null;

        try {
            animalResource = resourceLoader.getResource(animalFilePath);
            File file = animalResource.getFile();
            List<String> animalData = Files.readAllLines(file.toPath());

            try (Stream<String> stream = animalData.stream()) {

                stream.skip(1).map(line -> line.split(COMMA)).forEach(data -> {
                    Animal animal = new Animal();
                    animal.setLocated(Instant.now());
                    for (int i = 0; i < data.length; i++) {
                        switch (i) {
                            case 0:
                                animal.setTitle(data[i].toLowerCase());
                                break;

                            case 1:
                                animal.setType(data[i]);
                                break;
                            default:
                                animal.setPreference(Long.parseLong(data[i]));
                                break;
                        }
                    }
                    log.debug("Animal object to be stored --{}", animal);
                    animalRepository.save(animal);


                });
            }

        } catch (IOException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
        } finally {
            if (animalResource != null)
                animalResource.readableChannel().close();
        }
    }


    public void saveRoomData() throws IOException {
        Resource roomResource = null;

        try {
            roomResource = resourceLoader.getResource(roomFilePath);
            File file = roomResource.getFile();
            List<String> roomData = Files.readAllLines(file.toPath());

            try (Stream<String> stream = roomData.stream()) {

                stream.skip(1).map(line -> line.split(COMMA)).forEach(data -> {
                    Room room = new Room();
                    room.setCreated(Instant.now());
                    for (int i = 0; i < data.length; i++) {
                        if (i == 0) {
                            room.setTitle(data[i].toLowerCase());
                        } else {
                            room.setSize(Long.parseLong(data[i]));
                        }
                    }
                    log.debug("Room object to be stored --{}", room);
                    roomRepository.save(room);
                });
            }

        } catch (IOException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
        } finally {
            if (roomResource != null)
                roomResource.readableChannel().close();
        }
    }

    private void saveUserData() throws IOException {
        Resource userResource = null;

        try {
            userResource = resourceLoader.getResource(userFilePath);
            File file = userResource.getFile();
            List<String> userData = Files.readAllLines(file.toPath());

            try (Stream<String> stream = userData.stream()) {

                stream.skip(1).map(line -> line.split(COMMA)).forEach(data -> {
                    UserDTO user = new UserDTO();
                    user.setCreatedBy(Constants.CREATED_BY);
                    for (int i = 0; i < data.length; i++) {
                        switch (i) {
                            case 0:
                                user.setLogin(data[i].toLowerCase());
                                break;
                            case 1:
                                user.setEmail(data[i].toLowerCase());
                                break;
                            case 2:
                                user.setPassword(data[i]);
                                break;
                            case 3:
                                user.setFirstName(data[i].toLowerCase());
                                break;
                            case 4:
                                user.setLastName(data[i].toLowerCase());
                                break;
                            default:
                                user.setLangKey(data[i]);
                                break;
                        }
                    }
                    log.debug("User object to be stored --{}", user);
                    userService.registerUser(user);
                });
            }

        } catch (IOException e) {
            log.error(ERROR_OCCURRED, e.getMessage());
        } finally {
            if (userResource != null)
                userResource.readableChannel().close();
        }
    }
}
