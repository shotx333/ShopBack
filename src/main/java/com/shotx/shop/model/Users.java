package com.shotx.shop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter @Setter
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank private String username;
    @NotBlank @Email private String email;

    @NotBlank private String password;

    private String role = "USER";

    /* ─── Profile ─────────────────────────── */
    private String firstName;
    private String lastName;
    private Integer birthYear;
    private String phoneNumber;

    /** "MALE" | "FEMALE" | "OTHER" | null */
    private String gender;

    /** Relative URL to the avatar image (e.g. /uploads/avatars/23.png) */
    private String avatarUrl;
}
