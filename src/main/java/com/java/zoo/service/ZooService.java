package com.java.zoo.service;

import com.java.zoo.entity.Animal;
import com.java.zoo.entity.Favorite;
import com.java.zoo.entity.Room;
import com.java.zoo.repository.AnimalRepository;
import com.java.zoo.repository.FavoriteRepository;
import com.java.zoo.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ZooService {
    private static final Logger log = LoggerFactory.getLogger(ZooService.class);


    private final RoomRepository roomRepository;


    private final AnimalRepository animalRepository;


    private final FavoriteRepository favoriteRepository;

    public ZooService(RoomRepository roomRepository, AnimalRepository animalRepository, FavoriteRepository favoriteRepository) {
        this.roomRepository = roomRepository;
        this.animalRepository = animalRepository;
        this.favoriteRepository = favoriteRepository;
    }


    public void placeAnimal(Animal animal, Room room) {
        log.debug("Place animal service method animal id: {}, room id: {}", animal.getId(), room.getId());
        addAnimalToRoomAndSave(animal, room);
    }

    public void moveAnimal(Animal animal, Room room) {
        log.debug("Move animal service method animal id: {}, room id: {}", animal.getId(), room.getId());
        removeAssociatedAnimalAndSave(animal);
        addAnimalToRoomAndSave(animal, room);
    }

    public void deleteAnimalFromRoom(Animal animal) {
        log.debug("Delete animal service method animal id: {}", animal.getId());
        removeAssociatedAnimalAndSave(animal);
    }

    public void assignFavoriteRoom(Animal animal, Room room) {
        log.debug("Assign favorite room to animal service method animal id: {}, room id: {}", animal.getId(), room.getId());
        Favorite fav = new Favorite();
        fav.setRoomId(room.getId());
        fav.setAnimal(animal);
        animal.getFavorites().add(fav);
        animalRepository.saveAndFlush(animal);

    }

    public void unassignFavoriteRoom(Animal animal, Room room) {
        log.debug("UnAssign favorite room to animal service method animal id: {}, room id: {}", animal.getId(), room.getId());
        Favorite fav = favoriteRepository.findByRoomIdAndAnimalId(room.getId(), animal.getId());
        animal.getFavorites().remove(fav);
        animalRepository.saveAndFlush(animal);
        favoriteRepository.deleteById(fav.getId());
    }

    private void removeAssociatedAnimalAndSave(Animal animal) {
        Room presentRoom = animal.getRoom();
        presentRoom.getAnimals().remove(animal);
        roomRepository.saveAndFlush(presentRoom);
    }


    private void addAnimalToRoomAndSave(Animal animal, Room room) {
        room.getAnimals().add(animal);
        animal.setRoom(room);
        roomRepository.saveAndFlush(room);
    }

}
