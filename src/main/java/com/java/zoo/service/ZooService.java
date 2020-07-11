package com.java.zoo.service;

import com.java.zoo.entity.Animal;
import com.java.zoo.entity.Room;
import com.java.zoo.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZooService {
    private static final Logger log = LoggerFactory.getLogger(ZooService.class);

    @Autowired
    private RoomRepository roomRepository;

    public Room placeAnimal(Animal animal) {
        List<Room> matchingRooms;
        if (animal.getType().equals(">=")) {
            matchingRooms = roomRepository.findAllByAllotedIsFalseAndSizeGreaterThanEqualOrderBySizeAsc(animal.getPreference());
        } else {
            matchingRooms = roomRepository.findAllByAllotedIsFalseAndSizeLessThanEqualOrderBySizeDesc(animal.getPreference());
        }

        Room room = matchingRooms.get(0);
        room.getAnimals().add(animal);
        room.setAlloted(true);
        animal.setRoom(room);
        roomRepository.saveAndFlush(room);
        return room;
    }

    public Room moveAnimal(Animal animal,Long userPreferredRoom) {
        Room presentRoom = animal.getRoom();
        presentRoom.getAnimals().remove(animal);
        presentRoom.setAlloted(false);
        Room room;
        if(userPreferredRoom > 0){
            Optional<Room> roomOpt = roomRepository.findById(userPreferredRoom);
            room = roomOpt.get();
            room.getAnimals().add(animal);
            room.setAlloted(true);
            animal.setRoom(room);
            roomRepository.saveAndFlush(room);
        }else {
            List<Room> matchingRooms;
            if (animal.getType().equals(">=")) {
                matchingRooms = roomRepository.findAllByAllotedIsFalseAndSizeGreaterThanEqualOrderBySizeAsc(animal.getPreference());
            } else {
                matchingRooms = roomRepository.findAllByAllotedIsFalseAndSizeLessThanEqualOrderBySizeDesc(animal.getPreference());
            }

            room = matchingRooms.get(0);
            room.getAnimals().add(animal);
            animal.setRoom(room);
        }
        roomRepository.saveAndFlush(room);
        roomRepository.saveAndFlush(presentRoom);
        return room;
    }

    public void deleteAnimalFromRoom(Animal animal) {
        Room presentRoom = animal.getRoom();
        presentRoom.getAnimals().remove(animal);
        presentRoom.setAlloted(false);
        roomRepository.saveAndFlush(presentRoom);
    }
}
