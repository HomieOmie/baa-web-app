package com.homieomie.authservice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.homieomie.authservice.controllers.AuthController;

/**
 * Entry point for the {@code auth-service} AWS Lambda function.
 * <p>
 * Implements the {@link RequestHandler} interface to process incoming
 * {@link APIGatewayProxyRequestEvent} requests and return
 * {@link APIGatewayProxyResponseEvent} responses. Delegates request routing
 * to the {@link AuthController}.
 * </p>
 */
public class AuthServiceMain implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    /**
     * Controller responsible for routing and handling authentication-related requests.
     */
    private final AuthController authController = new AuthController();

    /**
     * Handles an incoming API Gateway request and delegates it to the {@link AuthController}.
     *
     * @param request the incoming request from API Gateway
     * @param context the AWS Lambda execution context
     * @return the response to be returned to API Gateway
     */
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        return authController.routeRequest(request);
    }
}
