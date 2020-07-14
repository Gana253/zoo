package com.java.zoo.repository;

import com.java.zoo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data mysql repository for the User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findOneByLogin(String login);


    Optional<User> findOneByEmailIgnoreCase(String email);
}

