package com.homieomie.authservice.models;

import jakarta.validation.constraints.NotBlank;

/**
 * Model representing a request to confirm a user signup.
 * <p>
 * This request typically includes the username and a confirmation
 * password or code provided by the user to complete the signup flow.
 * </p>
 */
public class ConfirmSignupRequest {

    /**
     * The username of the user confirming their signup.
     * Cannot be {@code null} or blank.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * The password or confirmation code provided by the user
     * to validate their signup. Cannot be {@code null} or blank.
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Default constructor for frameworks and deserialization.
     */
    public ConfirmSignupRequest() {}

    /**
     * Constructs a {@code ConfirmSignupRequest} with the given attributes.
     *
     * @param username the username of the user, must not be blank
     * @param password the confirmation password or code, must not be blank
     */
    public ConfirmSignupRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Returns the username.
     *
     * @return the username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the confirmation password or code.
     *
     * @return the confirmation password/code of the user
     */
    public String getPassword() {
        return password;
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
     * Sets the confirmation password or code.
     *
     * @param password the confirmation password/code, must not be blank
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
