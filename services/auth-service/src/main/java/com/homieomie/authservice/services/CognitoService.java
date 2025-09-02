package com.homieomie.authservice.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.homieomie.authservice.models.ConfirmSignupRequest;
import com.homieomie.authservice.models.LoginRequest;
import com.homieomie.authservice.models.SignupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CognitoService {
    private static final String USER_POOL_ID = System.getenv("USER_POOL_ID");
    private static final String CLIENT_ID = System.getenv("CLIENT_ID");

    private final CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.create();

    public String signup(SignupRequest req, Map<String, String> headers) {
        String authHeader = headers.get("Authorization");
        if (authHeader == null || !isAdmin(authHeader)) {
            throw new RuntimeException("Forbidden: admin access required");
        }

        AdminCreateUserRequest request = AdminCreateUserRequest.builder()
                .userPoolId(USER_POOL_ID)
                .username(req.getUsername())
                .userAttributes(
                        AttributeType.builder().name("email").value(req.getEmail()).build(),
                        AttributeType.builder().name("birthdate").value(req.getBirthdate()).build(),
                        AttributeType.builder().name("phone_number").value(req.getPhone_number()).build(),
                        AttributeType.builder().name("given_name").value(req.getFirst_name()).build(),
                        AttributeType.builder().name("family_name").value(req.getLast_name()).build(),
                        AttributeType.builder().name("sex").value(req.get_sex()).build()
                )
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .build();

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(request);
        return "User created: " + response.user().username();
    }

    private boolean isAdmin(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            List<String> groups = jwt.getClaim("cognito:groups").asList(String.class);
            return groups != null && groups.contains("admin");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean confirmSignup(ConfirmSignupRequest req) {
        try {
            AdminSetUserPasswordRequest request = AdminSetUserPasswordRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .username(req.getUsername())
                    .password(req.getPassword())
                    .permanent(true)  // make the password permanent
                    .build();

            AdminSetUserPasswordResponse _ = cognitoClient.adminSetUserPassword(request);

            return true; // success
        } catch (Exception e) {
            System.err.println("Error confirming signup for user " + req.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    public Map<String, String> login(LoginRequest req) {

        InitiateAuthRequest request = InitiateAuthRequest.builder()
                .clientId(CLIENT_ID)
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .authParameters(Map.of(
                        "USERNAME", req.getUsername(),
                        "PASSWORD", req.getPassword()
                ))
                .build();

        InitiateAuthResponse response = cognitoClient.initiateAuth(request);

        Map<String,String> tokens = new HashMap<>();
        tokens.put("idToken", response.authenticationResult().idToken());
        tokens.put("accessToken", response.authenticationResult().accessToken());
        tokens.put("refreshToken", response.authenticationResult().refreshToken());

        return tokens;
    }
}
