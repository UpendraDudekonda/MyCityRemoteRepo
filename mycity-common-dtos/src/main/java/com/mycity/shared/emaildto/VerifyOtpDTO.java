package com.mycity.shared.emaildto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyOtpDTO {

	private String email;
	private String otp;
}
