package com.java.zoo.web.util;


import com.java.zoo.dto.UserDTO;
import com.java.zoo.entity.User;
import org.springframework.stereotype.Service;

/**
 * Mapper for the entity {@link User} and its DTO called {@link UserDTO}.
 */
@Service
public class UserMapper {


    public User userDTOToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        } else {
            User user = new User();
            user.setId(userDTO.getId());
            user.setLogin(userDTO.getLogin());
            user.setFirstName(userDTO.getFirstName());
            user.setLastName(userDTO.getLastName());
            user.setEmail(userDTO.getEmail());
            user.setLangKey(userDTO.getLangKey());
            user.setCreatedBy(userDTO.getCreatedBy());
            return user;
        }
    }

}
