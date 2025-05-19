package com.mycity.otpvalidation.test;


import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.otpvalidation.exception.ExpiredOtpException;
import com.mycity.otpvalidation.exception.InvalidOtpException;
import com.mycity.otpvalidation.service.OtpService;
import com.mycity.shared.emaildto.VerifyOtpDTO;
import com.mycity.shared.responsedto.OTPResponse;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class OTPValidationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OtpService otpService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testVerifyOtp_validOtp() throws Exception {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        dto.setEmail("user@example.com");
        dto.setOtp("1234");

        Mockito.when(otpService.verifyOtp("user@example.com", "1234")).thenReturn(true);
       
        mockMvc.perform(post("/otp/auth/verifyotp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OTP verified successfully"))
                .andExpect(jsonPath("$.otpVerified").value(true));

    }

    @Test
    void testVerifyOtp_invalidOtp() throws Exception {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        dto.setEmail("user@example.com");
        dto.setOtp("wrongOtp");

        Mockito.doThrow(new InvalidOtpException("OTP does not match."))
               .when(otpService).verifyOtp("user@example.com", "wrongOtp");

        mockMvc.perform(post("/otp/auth/verifyotp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("OTP does not match."))
                .andExpect(jsonPath("$.otpVerified").value(false));
    }

    @Test
    void testVerifyOtp_expiredOtp() throws Exception {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        dto.setEmail("user@example.com");
        dto.setOtp("1234");

        doThrow(new ExpiredOtpException("OTP expired")).when(otpService).verifyOtp("user@example.com", "1234");

        mockMvc.perform(post("/otp/auth/verifyotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("OTP expired"))
                .andExpect(jsonPath("$.otpVerified").value(false));
    }

    @Test
    void testVerifyOtp_otpNotFound() throws Exception {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        dto.setEmail("user@example.com");
        dto.setOtp("1234");

        doThrow(new ExpiredOtpException("OTP expired or not found"))
            .when(otpService).verifyOtp("user@example.com", "1234");

        mockMvc.perform(post("/otp/auth/verifyotp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("OTP expired or not found"))
                .andExpect(jsonPath("$.otpVerified").value(false));
    }


    @Test
    void testVerifyOtp_internalServerError() throws Exception {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        dto.setEmail("user@example.com");
        dto.setOtp("1234");

        doThrow(new RuntimeException("Service is down")).when(otpService).verifyOtp("user@example.com", "1234");

        mockMvc.perform(post("/otp/auth/verifyotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error verifying OTP: Service is down"))
                .andExpect(jsonPath("$.otpVerified").value(false));

    }

    @Test
    void testVerifyOtp_missingEmail() throws Exception {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        dto.setOtp("1234");

        mockMvc.perform(post("/otp/auth/verifyotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testVerifyOtp_missingOtp() throws Exception {
        VerifyOtpDTO dto = new VerifyOtpDTO();
        dto.setEmail("user@example.com");

        mockMvc.perform(post("/otp/auth/verifyotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
