package com.java.zoo.service;


import com.java.zoo.ZooApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Serivce class for the application. To load data.
 */
@TestPropertySource(properties = {"inputfile.room-name=classpath:dataset/roomtest.csv", "inputfile.user-name=classpath:dataset/usertest.csv", "inputfile.animal-name=classpath:dataset/animaltest.csv"})
@SpringBootTest(classes = ZooApplication.class)
public class CommandLineServiceTest {

    @Autowired
    private CommandLineService commandLineService;

    @Test
    public void testRoomCsvErrorNotFound() {
        File file = new File(
                getClass().getClassLoader().getResource("dataset/roomtest.csv").getFile()
        );

        file.setReadable(false, false);

        IOException exception = assertThrows(IOException.class, () -> {
            commandLineService.saveRoomData();
        });

        file.setReadable(true, false);

    }

    @Test
    public void testAnimalCsvErrorNotFound() {
        File file = new File(
                getClass().getClassLoader().getResource("dataset/animaltest.csv").getFile()
        );

        file.setReadable(false, false);

        IOException exception = assertThrows(IOException.class, () -> {
            commandLineService.saveAnimalData();
        });

        file.setReadable(true, false);

    }

    @Test
    public void testUserCsvErrorNotFound() {
        File file = new File(
                Objects.requireNonNull(getClass().getClassLoader().getResource("dataset/usertest.csv")).getFile()
        );

        file.setReadable(false, false);

        IOException exception = assertThrows(IOException.class, () -> {
            commandLineService.loadUsers();
        });

        file.setReadable(true, false);

    }
}
