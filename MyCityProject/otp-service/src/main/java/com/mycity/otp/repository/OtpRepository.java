package com.mycity.otp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mycity.otp.entity.OTP;

public interface OtpRepository extends JpaRepository<OTP, Long> {

}
