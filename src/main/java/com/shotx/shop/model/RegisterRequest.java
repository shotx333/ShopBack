package com.shotx.shop.model;

import jakarta.validation.constraints.*;

public class RegisterRequest {

    @NotBlank  private String username;
    @Email     @NotBlank  private String email;
    @NotBlank  private String password;

    /* additional required profile info */
    @NotBlank  private String firstName;
    @NotBlank  private String lastName;
    @NotBlank  private String phoneNumber;

    /** MALE | FEMALE | OTHER */
    @Pattern(regexp = "MALE|FEMALE|OTHER") private String gender;

    @Min(1900) @Max(2100) private Integer birthYear;

    /* getters / setters */
    public String getUsername()        { return username; }
    public void   setUsername(String u){ this.username = u; }

    public String getEmail()           { return email; }
    public void   setEmail(String e)   { this.email = e; }

    public String getPassword()        { return password; }
    public void   setPassword(String p){ this.password = p; }

    public String getFirstName()       { return firstName; }
    public void   setFirstName(String f){ this.firstName = f; }

    public String getLastName()        { return lastName; }
    public void   setLastName(String l){ this.lastName = l; }

    public String getPhoneNumber()     { return phoneNumber; }
    public void   setPhoneNumber(String p){ this.phoneNumber = p; }

    public String getGender()          { return gender; }
    public void   setGender(String g)  { this.gender = g; }

    public Integer getBirthYear()      { return birthYear; }
    public void    setBirthYear(Integer b){ this.birthYear = b; }
}
