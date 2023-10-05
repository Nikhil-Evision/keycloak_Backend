package com.lirisoft.keycloak.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lirisoft.keycloak.model.*;
import com.lirisoft.keycloak.response.ApiResponse;
import com.lirisoft.keycloak.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;

import com.lirisoft.keycloak.service.LoginService;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.security.RolesAllowed;

@RestController
@EnableGlobalMethodSecurity(jsr250Enabled = true)
@RequestMapping("/api/user")
@CrossOrigin(origins = "*",allowedHeaders = "*")
public class UserController {

    @Autowired
    private LoginService loginservice;
    @Autowired
    private UserService userService;
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginrequest) {
        System.out.println("Inside login");
        return loginservice.login(loginrequest);
    }
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user, @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {
        System.out.println("inside admin user login");
        String[] parts = authorizationHeader.split(" ");
        String token = null;
        if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
            token = parts[1];
        }
        ApiResponse apiResponse = null;
        try {
            ResponseEntity<UserResponse> response = userService.addUser(user, token);
            if (response.getStatusCode().is2xxSuccessful()) {
                apiResponse = new ApiResponse(response.getStatusCode().toString(), "User has been created successfully");
            } else {
                System.out.println("Error while creating user :"+response.getStatusCode().toString());
                apiResponse = new ApiResponse(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Internal server error");
            }
        } catch (HttpClientErrorException e) {
            System.out.println("Exception occurred while creating users :" + e.getMessage());
            if (e.getStatusText().equalsIgnoreCase("Forbidden")) {
                System.out.println("Not authorized to perform this operation");
                apiResponse = new ApiResponse(e.getStatusText(), "Not authorized to perform this operation");
            } else if (e.getStatusText().equalsIgnoreCase("Conflict")) {
                System.out.println("Username is already available");
                apiResponse = new ApiResponse(e.getStatusText(), "Username is already available");
            }
        }
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Response> logout(@RequestBody TokenRequest token) {
        return loginservice.logout(token);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<IntrospectResponse> introspect(@RequestBody TokenRequest token) {
        return loginservice.introspect(token);
    }

    @PostMapping("/userInfo")
    public ResponseEntity<UserInfoResponse> userInfo(@RequestHeader("Authorization") String authorizationHeader){
        System.out.println("inside userInfo method");
        String[] parts = authorizationHeader.split(" ");
        String token = null;
        if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
            token = parts[1];
        }
        return userService.userInfo(token);
    }

    @PostMapping("/assign-role/{username}/{roleName}")
    @RolesAllowed("admin")
    public String assignRoleToUser(@PathVariable String username, @PathVariable String roleName) {
        userService.assignRoleToUserByUsername(username, roleName);
        return "Role assigned successfully.";
    }

    @PostMapping("/test-user")
    @RolesAllowed("test-admin")
    public String getUserAccessCheck(){
        return "allowed only to test-user";
    }

}
