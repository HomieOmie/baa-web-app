package com.homieomie.authservice.models;

import jakarta.validation.constraints.NotBlank;

public class ConfirmSignupRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;

    public ConfirmSignupRequest() {}

    public ConfirmSignupRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }
    public void setPassword(String password) { this.password = password; }
}