package com.homieomie.authservice.models;

import jakarta.validation.constraints.NotBlank;

/**
 * Model class representing a login request containing
 * the username and password of the user attempting to authenticate.
 */
public class LoginRequest {

    /**
     * The username provided by the user.
     * Cannot be {@code null} or blank.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The password provided by the user.
     * Cannot be {@code null} or blank.
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Default constructor for deserialization and frameworks.
     */
    public LoginRequest() {}

    /**
     * Constructs a {@code LoginRequest} with the specified username and password.
     *
     * @param username the username of the user, must not be blank
     * @param password the password of the user, must not be blank
     */
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the username.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Returns the password.
     *
     * @return the password of the user
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the username.
     *
     * @param username the username of the user, must not be blank
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password.
     *
     * @param password the password of the user, must not be blank
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
