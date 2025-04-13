package com.mycity.shared.userdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFinalRegRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private String mobilenumber;
    private boolean otpVerified;
}
