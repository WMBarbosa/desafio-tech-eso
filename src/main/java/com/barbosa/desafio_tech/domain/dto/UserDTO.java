package com.barbosa.desafio_tech.domain.dto;

import com.barbosa.desafio_tech.domain.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private Integer vbucks;
    private String password;


    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.vbucks = user.getVbucks();
        this.password = user.getPassword();
    }
}
