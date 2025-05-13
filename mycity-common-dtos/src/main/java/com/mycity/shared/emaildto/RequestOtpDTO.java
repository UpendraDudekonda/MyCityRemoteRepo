package com.mycity.shared.emaildto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestOtpDTO {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
	private String email;

}
