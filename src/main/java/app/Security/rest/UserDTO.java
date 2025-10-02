package app.Security.rest;

import app.Security.entities.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Getter
@ToString
@Builder

public class UserDTO {
    private String username;
    private String password;
    private Set<String > roles;

    public UserDTO(String username, String password, Set<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public UserDTO(){

    }

    public UserDTO(String username, Set<String> stringRoles) {
        this.username = username;
        this.roles = roles;
    }
}
