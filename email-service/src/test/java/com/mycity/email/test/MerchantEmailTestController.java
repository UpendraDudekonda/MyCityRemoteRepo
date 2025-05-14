package com.mycity.email.test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycity.email.controller.MerchantEmailController;
import com.mycity.email.service.EmailService;
import com.mycity.shared.emaildto.RequestOtpDTO;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class MerchantEmailTestController {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmailService merchantEmailService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGenerateOtp_validEmail() throws Exception {
        RequestOtpDTO dto = new RequestOtpDTO();
        dto.setEmail("user@example.com");

        mockMvc.perform(post("/email/merchant/generateotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP has been sent to your email."));

        Mockito.verify(merchantEmailService).generateAndSendOTP("user@example.com");
    }


    @Test
    public void testGenerateOtp_missingEmailField() throws Exception {
        mockMvc.perform(post("/email/merchant/generateotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is required"));
    }

    @Test
    public void testGenerateOtp_emptyEmail() throws Exception {
        RequestOtpDTO dto = new RequestOtpDTO();
        dto.setEmail("");

        mockMvc.perform(post("/email/merchant/generateotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is required"));
    }

    @Test
    public void testGenerateOtp_serviceThrowsError() throws Exception {
        RequestOtpDTO dto = new RequestOtpDTO();
        dto.setEmail("fail@example.com");

        Mockito.doThrow(new RuntimeException("Email service unavailable"))
                .when(merchantEmailService).generateAndSendOTP("fail@example.com");

        mockMvc.perform(post("/email/merchant/generateotp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Error generating OTP: Email service unavailable"));
    }
}



