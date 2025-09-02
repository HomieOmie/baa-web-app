package com.homieomie.authservice.controllers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homieomie.authservice.models.*;
import com.homieomie.authservice.services.CognitoService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class AuthController {

    private final CognitoService cognitoService = new CognitoService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Validator validator;

    public AuthController() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public APIGatewayProxyResponseEvent routeRequest(APIGatewayProxyRequestEvent request) {
        Map<String, Object> responseMap = new HashMap<>();
        int statusCode = 200;

        try {
            if ("OPTIONS".equalsIgnoreCase(request.getHttpMethod())) {
                return corsResponse();
            }

            Map<String, Object> body = objectMapper.readValue(request.getBody(), Map.class);
            String action = (String) body.get("action");

            switch (action) {
                case "signup" -> {
                    SignupRequest signupRequest = objectMapper.convertValue(body, SignupRequest.class);
                    validate(signupRequest);
                    responseMap.put("result", cognitoService.signup(signupRequest, request.getHeaders()));
                }
                case "confirmSignup" -> {
                    ConfirmSignupRequest confirmSignupRequest = objectMapper.convertValue(body, ConfirmSignupRequest.class);
                    validate(confirmSignupRequest);
                    responseMap.put("result", cognitoService.confirmSignup(confirmSignupRequest));
                }
                case "login" -> {
                    LoginRequest loginRequest = objectMapper.convertValue(body, LoginRequest.class);
                    validate(loginRequest);
                    responseMap.put("result", cognitoService.login(loginRequest));
                }
                default -> {
                    statusCode = 400;
                    responseMap.put("error", "Unknown action: " + action);
                }
            }

        } catch (Exception e) {
            statusCode = 500;
            responseMap.put("error", e.getMessage());
        }

        return buildResponse(statusCode, responseMap);
    }

    private void validate(Object dto) {
        Set<ConstraintViolation<Object>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .reduce((m1, m2) -> m1 + "; " + m2)
                    .orElse("Validation failed");
            throw new IllegalArgumentException(message);
        }
    }

    private APIGatewayProxyResponseEvent buildResponse(int statusCode, Map<String, Object> body) {
        try {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withHeaders(corsHeaders())
                    .withBody(objectMapper.writeValueAsString(body));
        } catch (Exception e) {
            return new APIGatewayProxyResponseEvent().withStatusCode(500).withBody("{\"error\":\"Serialization failed\"}");
        }
    }

    private Map<String, String> corsHeaders() {
        return Map.of(
                "Content-Type", "application/json",
                "Access-Control-Allow-Origin", "*",
                "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key",
                "Access-Control-Allow-Methods", "OPTIONS,POST,GET"
        );
    }

    private APIGatewayProxyResponseEvent corsResponse() {
        return new APIGatewayProxyResponseEvent().withStatusCode(200).withHeaders(corsHeaders()).withBody("");
    }
}
