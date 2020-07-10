package com.java.zoo.service;


import com.java.zoo.constants.Constants;
import com.java.zoo.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * Serivce class for the application. To load data.
 */
@Service
public class ZooService {

    public static final String COMMA = ";";

    private static final Logger log = LoggerFactory.getLogger(ZooService.class);


    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private UserService userService;

    @Value("${inputfile.user-name}")
    private String userFilePath;

    /**
     * Load User data on startup
     *
     * @throws IOException when CSV file is not found
     */
    public void loadUsers() throws IOException {
        //Load User from the user.csv file and persist in table
        saveUserData();
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
            log.error("Error Occurred -> {}", e.getMessage());
        } finally {
            if (userResource != null)
                userResource.readableChannel().close();
        }
    }
}
