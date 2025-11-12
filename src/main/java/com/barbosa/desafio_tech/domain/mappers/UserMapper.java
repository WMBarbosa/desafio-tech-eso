package com.barbosa.desafio_tech.domain.mappers;

import com.barbosa.desafio_tech.domain.dto.UserDTO;
import com.barbosa.desafio_tech.domain.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDto(User user);
    User toEntity(UserDTO userDTO);
}
