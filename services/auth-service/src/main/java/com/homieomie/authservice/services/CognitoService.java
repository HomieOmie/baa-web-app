package com.homieomie.authservice.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.homieomie.authservice.models.ConfirmSignupRequest;
import com.homieomie.authservice.models.LoginRequest;
import com.homieomie.authservice.models.SignupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.DeliveryMediumType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminCreateUserResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AdminSetUserPasswordRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.InitiateAuthResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class responsible for handling interactions with
 * Amazon Cognito, including user signup, confirmation,
 * and authentication flows.
 */
public class CognitoService {

    /**
     * Amazon Cognito User Pool ID for this application.
     * <p>
     * The value is loaded from the {@code USER_POOL_ID} environment variable
     * at application startup. If this environment variable is not set,
     * Cognito API calls may fail.
     */
    private static final String USER_POOL_ID = System.getenv("USER_POOL_ID");

    /**
     * Amazon Cognito App Client ID associated with the configured User Pool.
     * <p>
     * The value is loaded from the {@code CLIENT_ID} environment variable
     * at application startup. If this environment variable is not set,
     * authentication flows may fail.
     */
    private static final String CLIENT_ID = System.getenv("CLIENT_ID");

    /**
     * Cognito client used to perform operations against AWS Cognito.
     */
    private final CognitoIdentityProviderClient cognitoClient =
            CognitoIdentityProviderClient.create();

    /**
     * Creates a new user in the Cognito User Pool.
     *
     * @param req     the signup request containing user attributes
     * @param headers request headers, expected to contain an
     *                {@code Authorization} token with admin privileges
     * @return a string message indicating successful user creation
     * @throws RuntimeException if the request is not authorized
     */
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

    /**
     * Confirms a user's signup by setting their password permanently.
     *
     * @param req the confirmation request containing username and password
     * @return {@code true} if confirmation succeeds, {@code false} otherwise
     */
    public boolean confirmSignup(ConfirmSignupRequest req) {
        try {
            AdminSetUserPasswordRequest request = AdminSetUserPasswordRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .username(req.getUsername())
                    .password(req.getPassword())
                    .permanent(true)
                    .build();

            cognitoClient.adminSetUserPassword(request);
            return true;
        } catch (Exception e) {
            System.err.println("Error confirming signup for user "
                    + req.getUsername() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Authenticates a user and retrieves Cognito-issued tokens.
     *
     * @param req the login request containing username and password
     * @return a map containing ID, access, and refresh tokens
     */
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

        Map<String, String> tokens = new HashMap<>();
        tokens.put("idToken", response.authenticationResult().idToken());
        tokens.put("accessToken", response.authenticationResult().accessToken());
        tokens.put("refreshToken", response.authenticationResult().refreshToken());

        return tokens;
    }

    /**
     * Determines whether the given token belongs to a user
     * with admin privileges in Cognito.
     *
     * @param token the JWT token provided by Cognito
     * @return {@code true} if the user belongs to the admin group,
     *         {@code false} otherwise
     */
    private boolean isAdmin(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            List<String> groups = jwt.getClaim("cognito:groups").asList(String.class);
            return groups != null && groups.contains("admin");
        } catch (Exception e) {
            return false;
        }
    }
}
