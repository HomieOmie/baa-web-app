package com.homieomie.authservice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

class AuthServiceMainTest {

    private CognitoIdentityProviderClient mockCognito;
    private AuthServiceMain authService;

    @BeforeEach
    void setup() {
        mockCognito = mock(CognitoIdentityProviderClient.class);
        authService = new AuthServiceMain(mockCognito);

        // Default environment vars for test
        System.setProperty("USER_POOL_ID", "us-east-1_testpool");
        System.setProperty("CLIENT_ID", "test-client-id");
        System.setProperty("AWS_REGION", "us-east-1");
    }

    private APIGatewayProxyRequestEvent buildRequest(String body) {
        return new APIGatewayProxyRequestEvent().withBody(body);
    }

    // --- Signup ---
//    @Test
//    void testSignupSuccess() {
//        SignUpResponse mockResponse = SignUpResponse.builder()
//                .userConfirmed(true)
//                .build();
//        when(mockCognito.signUp(any(SignUpRequest.class))).thenReturn(mockResponse);
//
//        String body = "{\"action\":\"signup\",\"username\":\"bob\",\"password\":\"pass123\",\"email\":\"bob@example.com\"}";
//        APIGatewayProxyResponseEvent response = authService.handleRequest(buildRequest(body), mock(Context.class));
//
//        assertEquals(200, response.getStatusCode());
//        assertTrue(response.getBody().contains("User signup initiated"));
//        verify(mockCognito, times(1)).signUp(any(SignUpRequest.class));
//    }

//    // --- Confirm Signup ---
//    @Test
//    void testConfirmSignupSuccess() {
//        doNothing().when(mockCognito).confirmSignUp(any(ConfirmSignUpRequest.class));
//
//        String body = "{\"action\":\"confirmSignup\",\"username\":\"bob\",\"confirmationCode\":\"123456\"}";
//        APIGatewayProxyResponseEvent response = authService.handleRequest(buildRequest(body), mock(Context.class));
//
//        assertEquals(200, response.getStatusCode());
//        assertTrue(response.getBody().contains("User confirmed successfully."));
//        verify(mockCognito, times(1)).confirmSignUp(any(ConfirmSignUpRequest.class));
//    }
//
//    // --- Login ---
//    @Test
//    void testLoginSuccess() {
//        AuthenticationResultType resultType = AuthenticationResultType.builder()
//                .idToken("id123")
//                .accessToken("access123")
//                .refreshToken("refresh123")
//                .build();
//
//        AdminInitiateAuthResponse mockResponse = AdminInitiateAuthResponse.builder()
//                .authenticationResult(resultType)
//                .build();
//
//        when(mockCognito.adminInitiateAuth(any(AdminInitiateAuthRequest.class))).thenReturn(mockResponse);
//
//        String body = "{\"action\":\"login\",\"username\":\"bob\",\"password\":\"pass123\"}";
//        APIGatewayProxyResponseEvent response = authService.handleRequest(buildRequest(body), mock(Context.class));
//
//        assertEquals(200, response.getStatusCode());
//        assertTrue(response.getBody().contains("id123"));
//        assertTrue(response.getBody().contains("access123"));
//        assertTrue(response.getBody().contains("refresh123"));
//        verify(mockCognito, times(1)).adminInitiateAuth(any(AdminInitiateAuthRequest.class));
//    }
//
//    // --- Verify Token (invalid) ---
//    @Test
//    void testVerifyTokenInvalid() {
//        // invalid token triggers catch -> false
//        String body = "{\"action\":\"verify\",\"token\":\"invalid.jwt.token\"}";
//        APIGatewayProxyResponseEvent response = authService.handleRequest(buildRequest(body), mock(Context.class));
//
//        assertEquals(200, response.getStatusCode());
//        assertTrue(response.getBody().contains("Invalid token"));
//    }
//
//    // --- Unknown Action ---
//    @Test
//    void testUnknownAction() {
//        String body = "{\"action\":\"nonsense\"}";
//        APIGatewayProxyResponseEvent response = authService.handleRequest(buildRequest(body), mock(Context.class));
//
//        assertEquals(400, response.getStatusCode());
//        assertTrue(response.getBody().contains("Unknown action"));
//    }
//
//    // --- Malformed Body ---
//    @Test
//    void testMalformedJson() {
//        String body = "{not-json}";
//        APIGatewayProxyResponseEvent response = authService.handleRequest(buildRequest(body), mock(Context.class));
//
//        assertEquals(500, response.getStatusCode());
//        assertTrue(response.getBody().contains("error"));
//    }
}
