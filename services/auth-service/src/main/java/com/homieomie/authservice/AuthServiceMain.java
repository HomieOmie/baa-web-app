package com.homieomie.authservice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.Map;

public class AuthServiceMain implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String USER_POOL_ID = System.getenv("USER_POOL_ID");
    private static final String CLIENT_ID = System.getenv("CLIENT_ID");

    private final CognitoIdentityProviderClient cognitoClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthServiceMain() {
        this.cognitoClient = CognitoIdentityProviderClient.create();
    }

    public AuthServiceMain(CognitoIdentityProviderClient cognitoClient) {
        this.cognitoClient = cognitoClient;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        Map<String, Object> responseMap = new HashMap<>();
        int statusCode = 200;

        // Handle CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getHttpMethod())) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withHeaders(Map.of(
                            "Access-Control-Allow-Origin", "*",
                            "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key",
                            "Access-Control-Allow-Methods", "OPTIONS,POST,GET"
                    ))
                    .withBody("");
        }

        try {
            // Parse request body into a Map
            Map<String, Object> body = objectMapper.readValue(request.getBody(), Map.class);
            String action = (String) body.get("action");

            switch (action) {
                case "signup":
                    responseMap.put("result", signup(body));
                    break;
                case "confirmSignup":
                    // Extract username and password from the request body
                    String username = (String) body.get("username");
                    String password = (String) body.get("password");
                    boolean confirmed = confirmSignup(username, password);
                    responseMap.put("result", confirmed ? "User confirmed successfully." : "Failed to confirm user");
                    break;
                case "login":
                    responseMap.put("result", login(body));
                    break;
                default:
                    statusCode = 400;
                    responseMap.put("error", "Unknown action: " + action);
            }
        } catch (Exception e) {
            statusCode = 500;
            responseMap.put("error", e.getMessage());
        }

        try {
            String json = objectMapper.writeValueAsString(responseMap);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withHeaders(Map.of(
                            "Content-Type", "application/json",
                            "Access-Control-Allow-Origin", "*",
                            "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key",
                            "Access-Control-Allow-Methods", "OPTIONS,POST,GET"
                    ))
                    .withBody(json);

        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("{\"error\":\"Failed to serialize response\"}");
        }
    }

    private String signup(Map<String, Object> body) {
        String username = (String) body.get("username");
        String email = (String) body.get("email");
        String temporaryPassword = (String) body.get("password"); // can also generate randomly

        AdminCreateUserRequest request = AdminCreateUserRequest.builder()
                .userPoolId(USER_POOL_ID)
                .username(username)
                .userAttributes(
                        AttributeType.builder().name("email").value(email).build()
                )
                .desiredDeliveryMediums(DeliveryMediumType.EMAIL) // optional: send email if needed
                .build();

        AdminCreateUserResponse response = cognitoClient.adminCreateUser(request);

        return "User created: " + response.user().username();
    }

    public boolean confirmSignup(String username, String newPassword) {
        try {
            AdminSetUserPasswordRequest request = AdminSetUserPasswordRequest.builder()
                    .userPoolId(USER_POOL_ID)
                    .username(username)
                    .password(newPassword)
                    .permanent(true)  // make the password permanent
                    .build();

            AdminSetUserPasswordResponse response = cognitoClient.adminSetUserPassword(request);

            return true; // success
        } catch (Exception e) {
            System.err.println("Error confirming signup for user " + username + ": " + e.getMessage());
            return false;
        }
    }

    private Map<String, String> login(Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");

        try {
            Map<String, String> authParams = new HashMap<>();
            authParams.put("USERNAME", username);
            authParams.put("PASSWORD", password);

            InitiateAuthRequest request = InitiateAuthRequest.builder()
                    .clientId(CLIENT_ID)
                    .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .authParameters(authParams)
                    .build();

            InitiateAuthResponse result = cognitoClient.initiateAuth(request);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("idToken", result.authenticationResult().idToken());
            tokens.put("accessToken", result.authenticationResult().accessToken());
            tokens.put("refreshToken", result.authenticationResult().refreshToken());

            return tokens;

        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Login failed: " + e.awsErrorDetails().errorMessage());
        }
    }

}
