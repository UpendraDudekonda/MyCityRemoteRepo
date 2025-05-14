package com.mycity.email.test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.email.Exception.ExpiredOtpException;
import com.mycity.email.Exception.InvalidOtpException;
import com.mycity.email.service.EmailService;
import com.mycity.shared.emaildto.RequestOtpDTO;
import com.mycity.shared.emaildto.VerifyOtpDTO;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class EmailTestController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService emailService;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestOtpDTO requestOtpDTO;
    private VerifyOtpDTO verifyOtpDTO;

    @BeforeEach
    public void setUp() {
        requestOtpDTO = new RequestOtpDTO();
        requestOtpDTO.setEmail("user@example.com");

        verifyOtpDTO = new VerifyOtpDTO();
        verifyOtpDTO.setEmail("user@example.com");
        verifyOtpDTO.setOtp("1234");
    }

    // ===================== generateotp Tests =====================  

    @Test
    public void testGenerateOtp_success() throws Exception {
        Mockito.doNothing().when(emailService).generateAndSendOTP(anyString());

        mockMvc.perform(post("/email/auth/generateotp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestOtpDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP sent to email successfully."));
    }

    @Test
    public void testGenerateOtp_failure() throws Exception {
        Mockito.doThrow(new RuntimeException("Mail error")).when(emailService).generateAndSendOTP(anyString());

        mockMvc.perform(post("/email/auth/generateotp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestOtpDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to send OTP: Mail error"));
    }

    // ===================== verifyotp Tests =====================

    @Test
    public void testVerifyOtp_success() throws Exception {
        Mockito.when(emailService.verifyOTP("user@example.com", "1234")).thenReturn(true);

        mockMvc.perform(post("/email/auth/verifyotp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyOtpDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP verified successfully"))
                .andExpect(jsonPath("$.otpVerified").value(true));
    }

    @Test
    public void testVerifyOtp_invalidOtp() throws Exception {
        Mockito.when(emailService.verifyOTP("user@example.com", "1234"))
                .thenThrow(new InvalidOtpException("OTP does not match."));

        mockMvc.perform(post("/email/auth/verifyotp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyOtpDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error verifying OTP: OTP does not match."))
                .andExpect(jsonPath("$.otpVerified").value(false));
    }

    @Test
    public void testVerifyOtp_expiredOtp() throws Exception {
        Mockito.when(emailService.verifyOTP("user@example.com", "1234"))
                .thenThrow(new ExpiredOtpException("OTP expired."));

        mockMvc.perform(post("/email/auth/verifyotp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyOtpDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error verifying OTP: OTP expired."))
                .andExpect(jsonPath("$.otpVerified").value(false));
    }

    @Test
    public void testVerifyOtp_genericFailure() throws Exception {
        Mockito.when(emailService.verifyOTP("user@example.com", "1234"))
                .thenThrow(new RuntimeException("Redis error"));

        mockMvc.perform(post("/email/auth/verifyotp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyOtpDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error verifying OTP: Redis error"))
                .andExpect(jsonPath("$.otpVerified").value(false));
    }
}

