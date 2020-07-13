package com.java.zoo.repository;


import com.java.zoo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the Room entity.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {


}
