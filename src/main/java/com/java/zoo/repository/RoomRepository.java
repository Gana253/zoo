package com.java.zoo.repository;


import com.java.zoo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Room entity.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

   //
    List<Room> findAllByAllotedIsFalseAndSizeGreaterThanEqualOrderBySizeAsc(Long size);

    List<Room> findAllByAllotedIsFalseAndSizeLessThanEqualOrderBySizeDesc(Long size);
}
