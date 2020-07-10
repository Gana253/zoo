package com.java.zoo.service;


import com.java.zoo.constants.Constants;
import com.java.zoo.dto.UserDTO;
import com.java.zoo.entity.User;
import com.java.zoo.exception.InvalidPasswordException;
import com.java.zoo.repository.UserRepository;
import com.java.zoo.util.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public User registerUser(UserDTO userDTO) {

        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setLangKey(userDTO.getLangKey());
        newUser.setCreatedBy(userDTO.getCreatedBy());
        userRepository.save(newUser);
        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }


    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<UserDTO> updateUser(UserDTO userDTO) {
        return Optional
                .of(userRepository.findById(userDTO.getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(
                        user -> {
                            user.setLogin(userDTO.getLogin().toLowerCase());
                            user.setFirstName(userDTO.getFirstName());
                            user.setLastName(userDTO.getLastName());
                            if (userDTO.getEmail() != null) {
                                user.setEmail(userDTO.getEmail().toLowerCase());
                            }
                            user.setLangKey(userDTO.getLangKey());
                            userRepository.save(user);
                            log.debug("Changed Information for User: {}", user);
                            return user;
                        }
                )
                .map(UserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository
                .findOneByLogin(login)
                .ifPresent(
                        user -> {
                            userRepository.delete(user);
                            log.debug("Deleted User: {}", user);
                        }
                );
    }


    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils
                .getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .ifPresent(
                        user -> {
                            String currentEncryptedPassword = user.getPassword();
                            if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                                throw new InvalidPasswordException();
                            }
                            String encryptedPassword = passwordEncoder.encode(newPassword);
                            user.setPassword(encryptedPassword);
                            userRepository.save(user);
                            log.debug("Changed password for User: {}", user);
                        }
                );
    }

    public List<UserDTO> getAllManagedUsers() {
        return userRepository
                .findAll()
                .stream()
                .filter(user -> !Constants.ANONYMOUS_USER.equals(user.getLogin()))
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }


    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneByLogin(login);
    }

}
