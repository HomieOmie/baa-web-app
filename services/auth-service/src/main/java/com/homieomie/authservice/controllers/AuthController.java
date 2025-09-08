package com.homieomie.authservice.controllers;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homieomie.authservice.models.LoginRequest;
import com.homieomie.authservice.models.ConfirmSignupRequest;
import com.homieomie.authservice.models.SignupRequest;
import com.homieomie.authservice.services.CognitoService;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Controller responsible for routing authentication-related requests
 * to the appropriate service methods. Acts as a dispatcher between
 * API Gateway events and AWS Cognito service calls.
 */
public class AuthController {

    /**
     * Service responsible for interacting with AWS Cognito.
     */
    private final CognitoService cognitoService = new CognitoService();

    /**
     * JSON mapper used for converting request bodies into model objects.
     */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Validator used to enforce model constraints.
     */
    private final Validator validator;

    /**
     * Constructs an {@code AuthController} and initializes
     * the validator for request model validation.
     */
    public AuthController() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    /**
     * Routes an incoming API Gateway request to the appropriate
     * authentication handler based on the provided action.
     *
     * @param request the API Gateway request event
     * @return the API Gateway response event containing the result
     */
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
                    ConfirmSignupRequest confirmSignupRequest = objectMapper.convertValue(
                            body, ConfirmSignupRequest.class);
                    validate(confirmSignupRequest);
                    responseMap.put("result", cognitoService.confirmSignup(confirmSignupRequest));
                }
                case "login" -> {
                    LoginRequest loginRequest = objectMapper.convertValue(body, LoginRequest.class);
                    validate(loginRequest);
                    responseMap.put("result", cognitoService.login(loginRequest));
                }
                case "listUsers" -> {
                    responseMap.put("result", cognitoService.listUsers(request.getHeaders()));
                }
                default -> {
                    statusCode = 400;
                    responseMap.put("error", "Unknown action: " + action);
                }
            }
        } catch (JsonProcessingException e) {   // Jackson parsing
            statusCode = 400;
            responseMap.put("error", "Invalid JSON: " + e.getOriginalMessage());
        } catch (IllegalArgumentException e) {   // validation failures
            statusCode = 400;
            responseMap.put("error", e.getMessage());
        } catch (RuntimeException e) {           // unexpected runtime issues
            statusCode = 500;
            responseMap.put("error", e.getMessage());
        }

        return buildResponse(statusCode, responseMap);
    }

    /**
     * Validates a given request DTO against its declared constraints.
     *
     * @param dto the object to validate
     * @throws IllegalArgumentException if validation fails
     */
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

    /**
     * Builds an API Gateway response object with the given
     * status code and body.
     *
     * @param statusCode the HTTP status code
     * @param body the response body
     * @return the constructed API Gateway response event
     */
    private APIGatewayProxyResponseEvent buildResponse(int statusCode, Map<String, Object> body) {
        try {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withHeaders(corsHeaders())
                    .withBody(objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) { // narrowed
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody("{\"error\":\"Serialization failed\"}");
        }
    }

    /**
     * Returns CORS headers required for API Gateway responses.
     *
     * @return a map of CORS headers
     */
    private Map<String, String> corsHeaders() {
        return Map.of(
                "Content-Type", "application/json",
                "Access-Control-Allow-Origin", "*",
                "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key",
                "Access-Control-Allow-Methods", "OPTIONS,POST,GET"
        );
    }

    /**
     * Builds a simple CORS preflight response for API Gateway.
     *
     * @return the API Gateway response event for CORS
     */
    private APIGatewayProxyResponseEvent corsResponse() {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(200)
                .withHeaders(corsHeaders())
                .withBody("");
    }
}
