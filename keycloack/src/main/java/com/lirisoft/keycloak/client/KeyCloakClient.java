//package com.lirisoft.keycloak.client;
//
//import org.keycloak.OAuth2Constants;
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//@Configuration
//public class KeyCloakClient {
//
//    private Keycloak keycloak = null;
//
//    @Value("${keycloak.auth-server-url}")
//    private String serverURL;
//
//    @Value("${keycloak.realm}")
//    private String realm;
//
//    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.client-id}")
//    private String clientId;
//
//    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.client-secret}")
//    private String clientSecret;
//
//    @Value("${spring.security.oauth2.client.registration.oauth2-client-credentials.authorization-grant-type}")
//    private String grantType;
//
//    public Keycloak getInstance() {
//
//        if (keycloak == null) {
//            return KeycloakBuilder.builder()
//                    .serverUrl(serverURL)
//                    .realm(realm)
//                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                    .clientId(clientId)
//                    .clientSecret(clientSecret)
//                    .build();
//        }
//        return keycloak;
//    }
//
////    public Keycloak newKeycloakBuilderWithPasswordCredentials(String username, String password) {
////        return KeycloakBuilder.builder() //
////                .realm(realm) //
////                .serverUrl(serverURL)//
////                .clientId(clientID) //
////                .clientSecret(null) //
////                .username(username) //
////                .password(password)
////                .scope("openid")
//////                .resteasyClient(new ResteasyClientBuilder()
//////                        .connectionPoolSize(10)
//////                        .build()
//////                )
////                .build();
////    }
//}
