package com.mycity.email.test;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.email.service.EmailService;
import com.mycity.shared.emaildto.RequestOtpDTO;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserEmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService userEmailService;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ 1. Valid email
    @Test
    void testGenerateOtp_validEmail() throws Exception {
        RequestOtpDTO dto = new RequestOtpDTO();
        dto.setEmail("user@example.com");

        mockMvc.perform(post("/email/user/generateotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP has been sent to your email."));

        Mockito.verify(userEmailService).generateAndSendOTP("user@example.com");
    }

    // ✅ 2. Missing email field (empty JSON)
    @Test
    void testGenerateOtp_missingEmailField() throws Exception {
        mockMvc.perform(post("/email/user/generateotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is required"));
    }

    // ✅ 3. Empty email string
    @Test
    void testGenerateOtp_emptyEmail() throws Exception {
        RequestOtpDTO dto = new RequestOtpDTO();
        dto.setEmail("");

        mockMvc.perform(post("/email/user/generateotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is required"));
    }

    // ✅ 4. Invalid email format
    @Test
    void testGenerateOtp_invalidEmailFormat() throws Exception {
        RequestOtpDTO dto = new RequestOtpDTO();
        dto.setEmail("invalid-email");

        mockMvc.perform(post("/email/user/generateotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email should be valid"));
    }

    // ✅ 5. Email service throws internal error
    @Test
    void testGenerateOtp_serviceThrowsError() throws Exception {
        RequestOtpDTO dto = new RequestOtpDTO();
        dto.setEmail("fail@example.com");

        Mockito.doThrow(new RuntimeException("Email service unavailable"))
                .when(userEmailService).generateAndSendOTP("fail@example.com");

        mockMvc.perform(post("/email/user/generateotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error generating OTP: Email service unavailable"));
    }
}

