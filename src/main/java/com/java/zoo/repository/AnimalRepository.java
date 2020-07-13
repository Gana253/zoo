package com.java.zoo.repository;


import com.java.zoo.dto.AnimalsJsonObject;
import com.java.zoo.dto.HappyAnimalsJsonObject;
import com.java.zoo.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Animal entity.
 */
@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
    List<AnimalsJsonObject> findAllByRoomIsNullOrderByLocatedDesc();

    List<AnimalsJsonObject> findAllByRoom_IdEqualsOrderByLocatedDesc(Long roomId);

    @Query(value = "select a.room.title AS Roomtitle,SUM(Case When a.type = '<=' and a.room.size <= a.preference THEN " +
            " 1  when a.type = '>=' and  a.room.size >= a.preference THEN 1 ELSE 0 END) AS " +
            "HappyAnimals from Animal a where a.room.id = a.room.id Group By a.room.title")
    List<HappyAnimalsJsonObject> findAllHappyAnimals();
}
