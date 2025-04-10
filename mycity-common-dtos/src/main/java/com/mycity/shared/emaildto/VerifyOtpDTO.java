package com.mycity.shared.emaildto;

import lombok.Data;

@Data
public class VerifyOtpDTO {

	private String email;
	private String otp;
}
