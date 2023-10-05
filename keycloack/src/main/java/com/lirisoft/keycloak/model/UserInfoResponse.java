package com.lirisoft.keycloak.model;

import lombok.Data;

@Data
public class UserInfoResponse {
    private String sub;
    private Boolean email_verified;
    private String name;
    private String preferred_username;
    private String given_name;
    private String family_name;
    private String email;
}
