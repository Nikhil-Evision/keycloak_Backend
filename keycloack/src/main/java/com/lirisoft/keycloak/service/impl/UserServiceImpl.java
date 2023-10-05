package com.lirisoft.keycloak.service.impl;

import com.lirisoft.keycloak.model.User;
import com.lirisoft.keycloak.model.UserInfoResponse;
import com.lirisoft.keycloak.model.UserResponse;
import com.lirisoft.keycloak.service.UserService;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.shaded.json.parser.JSONParser;
import com.nimbusds.jose.shaded.json.parser.ParseException;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String serverURL;

    @Value("${spring.security.oauth2.client.provider.keycloak.issuer-uri}")
    private String issueUrl;

    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.authorization-grant-type}")
    private String grantType;

    @Value("${spring.security.oauth2.client.provider.keycloak.user-uri}")
    private String userApiURL;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public ResponseEntity<UserResponse> addUser(User user, String token) throws HttpClientErrorException {
        CredentialRepresentation credential = createPasswordCredentials(user.getPassword());
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUserName());
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setEmail(user.getEmailId());
        userRepresentation.setCredentials(Collections.singletonList(credential));
        userRepresentation.setEnabled(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
//        fetchUser("abc");
        HttpEntity<UserRepresentation> httpEntity = new HttpEntity<>(userRepresentation, headers);
        return restTemplate.postForEntity(userApiURL, httpEntity, UserResponse.class);
    }

    @Override
    public ResponseEntity<UserInfoResponse> userInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken); // Include the access token

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(map, headers);

        ResponseEntity<UserInfoResponse> response = restTemplate
                .postForEntity(issueUrl + "/protocol/openid-connect/userinfo", httpEntity, UserInfoResponse.class);

        return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<String>> getUserRole(String accessToken) throws ParseException {

        String token = accessToken;
        String[] parts = token.split("\\.");
        String payload = parts[1];
        System.out.println("Payload: " + payload);

        byte[] payloadBytes = java.util.Base64.getDecoder().decode(payload);
        String payloadJsonString = new String(payloadBytes);
        System.out.println("Payload JSON String: " + payloadJsonString);
        try {
            JSONParser parser = new JSONParser();
            JSONObject payloadJson = (JSONObject) parser.parse(payloadJsonString);
            JSONArray rolesArray = (JSONArray) ((JSONObject) payloadJson.get("realm_access")).get("roles");
            List<String> rolesList = new ArrayList<>();
            for (Object role : rolesArray) {
                rolesList.add(role.toString());
            }
            String subject = (String) payloadJson.get("sub");
            String roles = ((JSONObject) payloadJson.get("realm_access")).get("roles").toString();
            System.out.println("Subject: " + subject);
            System.out.println("Roles: " + roles);
            return new ResponseEntity<>(rolesList, HttpStatus.OK);
        } catch (Exception e) {
            System.out.println("Error parsing JSON: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Return an appropriate response for the error
        }
    }
    private static CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

    @Override
    public void assignRoleToUserByUsername(String username, String roleName) {

    }

    //    @Override
//    public void assignRoleToUserByUsername(String username, String roleName) {
//        RealmResource realmResource = keyCloakClient.getInstance().realm(realm);
//        UsersResource usersResource = realmResource.users();
//
//        // Retrieve the user by username
//        List<UserRepresentation> users = usersResource.search(username, true);
//        if (users.isEmpty()) {
//            throw new IllegalArgumentException("User not found.");
//        }
//
//        // Retrieve the role by rolename
//        RoleResource roleResource = realmResource.roles().get(roleName);
//        if (roleResource == null) {
//            throw new IllegalArgumentException("Role not found.");
//        }
//
//        // Assign the role to the user
//        UserResource userResource = usersResource.get(users.get(0).getId());
//        userResource.roles().realmLevel().add(Arrays.asList(roleResource.toRepresentation()));
//    }


//    private void fetchUser(String userName) {
//        UserModel userModel = new UserModel();
//        userModel.setEmail("test@gmail.com");
//        userModel.setUserName("testuser");
//        userModel.setAddress("testaddress");
//        userModel.setEnable(true);
//        userModel.setOnboardDate(new Date());
//        System.out.println("going to add user ::");
//        adapterService.addUser(userModel);
//        System.out.println("going to fetch user ::");
//        Optional<UserModel> userByUserName = adapterService.findUserByUserName("testuser");
//        System.out.println("userName  :"+userByUserName);
//
//    }
}
