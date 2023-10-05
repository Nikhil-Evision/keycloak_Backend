package com.lirisoft.keycloak.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lirisoft.keycloak.model.User;
import com.lirisoft.keycloak.model.UserInfoResponse;
import com.lirisoft.keycloak.model.UserResponse;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<UserResponse> addUser(User user, String token) throws JsonProcessingException;
    void assignRoleToUserByUsername(String username, String roleName);
    ResponseEntity<UserInfoResponse> userInfo(String accessToken);
    ResponseEntity<List<String>> getUserRole(String accessToken) throws ParseException;

}
