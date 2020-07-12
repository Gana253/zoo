package com.java.zoo.repository;


import com.java.zoo.dto.AnimalsJsonObject;
import com.java.zoo.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Animal entity.
 */
@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<AnimalsJsonObject> findAllByRoomIsNullOrderByLocatedDesc();

    List<AnimalsJsonObject> findAllByRoom_IdEqualsOrderByLocatedDesc(Long roomId);
}
