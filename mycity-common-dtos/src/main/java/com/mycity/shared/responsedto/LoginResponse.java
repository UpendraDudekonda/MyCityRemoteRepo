package com.mycity.shared.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private Boolean status;
    private String message;
    private Long id;
    private String email;
    private String role;
}