package com.lirisoft.keycloak.model;

import lombok.Data;

@Data
public class    User {
    private String userName;
    private String emailId;
    private String password;
    private String firstName;
    private String lastName;
}
