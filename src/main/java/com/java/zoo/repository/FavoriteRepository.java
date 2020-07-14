package com.java.zoo.repository;


import com.java.zoo.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Favorite entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Favorite findByRoomIdAndAnimalId(Long roomId, Long animalId);

    @Query(value = "select  r.title from Room r,  Favorite f where f.animal.id = ?1 and r.id = f.roomId")
    List<String> findFavoriteRooms(Long animalId);
}
