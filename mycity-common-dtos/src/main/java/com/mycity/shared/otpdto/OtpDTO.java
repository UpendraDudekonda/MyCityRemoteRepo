package com.mycity.shared.otpdto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpDTO {
	
	private Long otpId;
	private String userId;
	private String otp;
	private LocalDateTime expiryTime;
	private boolean verified;
	
}
