package com.shotx.shop.model;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateProfileRequest {

    @Size(max = 50)  private String firstName;
    @Size(max = 50)  private String lastName;
    @Min(1900)       private Integer birthYear;
    @Size(max = 20)  private String phoneNumber;

    /** MALE | FEMALE | OTHER | null */
    private String gender;
}
