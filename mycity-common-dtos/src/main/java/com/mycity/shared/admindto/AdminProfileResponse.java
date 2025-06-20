package com.mycity.shared.admindto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminProfileResponse {

    private Long id;
    private String email;
    private String role;
    private String username;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    private String country;
    private String city;
    private String postalCode;
}
