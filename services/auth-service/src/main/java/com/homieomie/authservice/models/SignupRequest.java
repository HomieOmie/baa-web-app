package com.homieomie.authservice.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Model class representing the attributes provided during a user signup request.
 * <p>
 * Includes validation annotations to ensure that input data matches expected
 * formats and business rules.
 * </p>
 */
public class SignupRequest {

    /**
     * The chosen username of the user.
     * Must be between 3 and 30 characters.
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    /**
     * The email address of the user.
     * Must be valid according to RFC email standards.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    /**
     * The user's birthdate.
     * Must be provided in E.164 phone number format (e.g., {@code +15551234567}).
     * <p>
     * Note: This field appears mislabeled and may represent a phone number instead.
     * </p>
     */
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Birthday must be in the format YYYY-MM-DD")
    private String birthdate;

    /**
     * The user's phone number.
     * Must follow the {@code YYYY-MM-DD} format (year-month-day).
     * <p>
     * Note: This field appears mislabeled and may represent a birthdate instead.
     * </p>
     */
    @NotBlank(message = "Birthday is required")
    @Pattern(regexp = "^\\+[1-9]\\d{1,14}$", message = "Phone number must be in E.164 format, e.g. +15551234567")
    private String phoneNumber;

    /**
     * The first name of the user.
     */
    @NotBlank(message = "First name is required")
    private String firstName;

    /**
     * The last name of the user.
     */
    @NotBlank(message = "Last name is required")
    private String lastName;

    /**
     * The user's sex, must be one of: male, female, or other.
     */
    @NotBlank(message = "Sex is required")
    @Pattern(regexp = "^(male|female|other)$", message = "Sex must be male, female, or other")
    private String sex;

    /**
     * Default no-argument constructor.
     */
    public SignupRequest() {}

    /**
     * Constructs a new {@link SignupRequest} with all required fields.
     *
     * @param username    the username of the user
     * @param email       the email address of the user
     * @param birthdate   the user's birthdate (or phone number, depending on intent)
     * @param phoneNumber the user's phone number (or birthdate, depending on intent)
     * @param firstName   the user's first name
     * @param lastName    the user's last name
     * @param sex         the user's sex
     */
    public SignupRequest(String username, String email, String birthdate,
                         String phoneNumber, String firstName,
                         String lastName, String sex) {
        this.username = username;
        this.email = email;
        this.birthdate = birthdate;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.sex = sex;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets the email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Gets the birthdate (or phone number, depending on field intent).
     *
     * @return the birthdate
     */
    public String getBirthdate() {
        return this.birthdate;
    }

    /**
     * Gets the phone number (or birthdate, depending on field intent).
     *
     * @return the phone number
     */
    public String getPhone_number() {
        return this.phoneNumber;
    }

    /**
     * Gets the first name.
     *
     * @return the first name
     */
    public String getFirst_name() {
        return this.firstName;
    }

    /**
     * Gets the last name.
     *
     * @return the last name
     */
    public String getLast_name() {
        return this.lastName;
    }

    /**
     * Gets the sex.
     *
     * @return the sex
     */
    public String get_sex() {
        return this.sex;
    }

    /**
     * Sets the username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the email address.
     *
     * @param email the email address
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Sets the birthdate (or phone number, depending on intent).
     *
     * @param birthdate the birthdate
     */
    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    /**
     * Sets the phone number (or birthdate, depending on intent).
     *
     * @param phoneNumber the phone number
     */
    public void setPhone_number(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Sets the first name.
     *
     * @param firstName the first name
     */
    public void setFirst_name(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Sets the last name.
     *
     * @param lastName the last name
     */
    public void setLast_name(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Sets the sex.
     *
     * @param sex the sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }
}
