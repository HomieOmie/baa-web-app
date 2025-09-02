package com.homieomie.authservice.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format, e.g. +15551234567")
    private String birthdate;

    @NotBlank(message = "Birthday is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Birthday must be in the format YYYY-MM-DD")
    private String phone_number;

    @NotBlank(message = "First name is required")
    private String first_name;

    @NotBlank(message = "Last name is required")
    private String last_name;

    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "^(male|female|other)$", message = "Sex must be male, female, or other")
    private String sex;


    public SignupRequest() {}

    public SignupRequest(String username, String email, String birthdate, String phone_number, String first_name, String last_name, String sex) {
        this.username = username;
        this.email = email;
        this.birthdate = birthdate;
        this.phone_number = phone_number;
        this.first_name = first_name;
        this.last_name = last_name;
        this.sex = sex;
    }

    public String getUsername() { return this.username; }
    public String getEmail() { return this.email; }
    public String getBirthdate() { return this.birthdate; }
    public String getPhone_number() { return this.phone_number; }
    public String getFirst_name() { return this.first_name; }
    public String getLast_name() { return this.last_name; }
    public String get_sex() { return this.sex; }

    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }
    public void setPhone_number(String phone_number) { this.phone_number = phone_number; }
    public void setFirst_name(String first_name) { this.first_name = first_name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }
    public void setSex(String sex) { this.sex = sex; }
}
