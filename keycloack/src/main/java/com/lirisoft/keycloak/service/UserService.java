package com.lirisoft.keycloak.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lirisoft.keycloak.model.User;
import com.lirisoft.keycloak.model.UserInfoResponse;
import com.lirisoft.keycloak.model.UserResponse;
import org.springframework.http.ResponseEntity;

public interface UserService {
    ResponseEntity<UserResponse> addUser(User user, String token) throws JsonProcessingException;
    void assignRoleToUserByUsername(String username, String roleName);
    ResponseEntity<UserInfoResponse> userInfo(String accessToken);

}
