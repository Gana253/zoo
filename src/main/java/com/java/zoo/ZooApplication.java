package com.java.zoo;

import com.java.zoo.service.CommandLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ZooApplication implements CommandLineRunner {
    @Autowired
    private CommandLineService commandLineService;

    @Value("${load.default-data}")
    private boolean loadData;

    public static void main(String[] args) {
        SpringApplication.run(ZooApplication.class, args);
    }


    public void run(String... params) throws IOException {
        commandLineService.loadUsers();
        if (loadData) {
            //Load default data for room and animal. This flag will be false by default
            commandLineService.saveRoomData();
            commandLineService.saveAnimalData();
        }
    }
}
