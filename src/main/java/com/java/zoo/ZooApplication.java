package com.java.zoo;

import com.java.zoo.service.ZooService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ZooApplication implements CommandLineRunner {
    @Autowired
    private ZooService zooService;

    public static void main(String[] args) {
        SpringApplication.run(ZooApplication.class, args);
    }


    public void run(String... params) throws IOException {
        zooService.loadUsers();

    }
}
