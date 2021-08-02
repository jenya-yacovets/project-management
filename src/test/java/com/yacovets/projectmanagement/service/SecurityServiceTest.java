package com.yacovets.projectmanagement.service;

import com.yacovets.projectmanagement.entity.EmailToken;
import com.yacovets.projectmanagement.entity.EmailTokenTypeEnum;
import com.yacovets.projectmanagement.repository.EmailTokenRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class SecurityServiceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmailTokenRepository emailTokenRepository;

    @Test
    @Order(1)
    void registrationView() throws Exception {
        mockMvc.perform(
                get("/security/registration")
                        .accept(MediaType.TEXT_HTML)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(2)
    void registrationHandler() throws Exception {
        mockMvc.perform(
                post("/security/registration")
                        .accept(MediaType.TEXT_HTML)
                        .param("email", "Alex.email@dev.ioc")
                        .param("username", "Alex")
                        .param("password", "12345678")
                        .param("passwordConfirmation", "12345678")
                        .with(csrf())
        ).andExpect(redirectedUrl("/security/authentication"));
    }

    @Test
    @Order(3)
    void authenticationView() throws Exception {
        mockMvc.perform(
                get("/security/authentication")
                        .accept(MediaType.TEXT_HTML)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void authenticationEmailHandler() throws Exception {
        mockMvc.perform(
                post("/security/authentication")
                        .accept(MediaType.TEXT_HTML)
                        .param("emailOrUsername", "Alex.email@dev.ioc")
                        .param("password", "12345678")
                        .with(csrf())
        ).andExpect(redirectedUrl("/home"));
    }

    @Test
    @Order(4)
    void authenticationUsernameHandler() throws Exception {
        mockMvc.perform(
                post("/security/authentication")
                        .accept(MediaType.TEXT_HTML)
                        .param("emailOrUsername", "Alex")
                        .param("password", "12345678")
                        .with(csrf())
        ).andExpect(redirectedUrl("/home"));
    }

    @Test
    @Order(5)
    void emailVerification() throws Exception {
        EmailToken emailToken = emailTokenRepository.findByUserUsernameAndType("Alex", EmailTokenTypeEnum.EMAIL_CONFIRMATION).orElseThrow(Exception::new);

        mockMvc.perform(
                get(String.format("/security/email-verification/%s", emailToken.getId()))
                        .accept(MediaType.TEXT_HTML)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(6)
    void passwordRecoveryView() throws Exception {
        mockMvc.perform(
                get("/security/password-recovery")
                        .accept(MediaType.TEXT_HTML)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(7)
    void passwordRecoveryHandler() throws Exception {
        mockMvc.perform(
                post("/security/password-recovery")
                        .accept(MediaType.TEXT_HTML)
                        .param("email", "Alex.email@dev.ioc")
                        .param("username", "Alex")
                        .with(csrf())
        ).andExpect(status().isOk());
    }

    @Test
    @Order(8)
    void setPasswordRecoveryView() throws Exception {
        EmailToken emailToken = emailTokenRepository.findByUserUsernameAndType("Alex", EmailTokenTypeEnum.PASSWORD_RECOVERY).orElseThrow(Exception::new);

        mockMvc.perform(
                get(String.format("/security/password-recovery/%s", emailToken.getId()))
                        .accept(MediaType.TEXT_HTML)
        ).andExpect(status().isOk());
    }

    @Test
    @Order(9)
    void setPasswordRecoveryHandler() throws Exception {
        EmailToken emailToken = emailTokenRepository.findByUserUsernameAndType("Alex", EmailTokenTypeEnum.PASSWORD_RECOVERY).orElseThrow(Exception::new);

        mockMvc.perform(
                post(String.format("/security/password-recovery/%s", emailToken.getId()))
                        .accept(MediaType.TEXT_HTML)
                        .param("password", "9999999999")
                        .param("passwordConfirmation", "9999999999")
                        .with(csrf())
        ).andExpect(redirectedUrl("/security/authentication"));
    }

    @Test
    @Order(10)
    void authenticationAfterSetPasswordHandler() throws Exception {
        mockMvc.perform(
                post("/security/authentication")
                        .accept(MediaType.TEXT_HTML)
                        .param("emailOrUsername", "Alex")
                        .param("password", "9999999999")
                        .with(csrf())
        ).andExpect(redirectedUrl("/home"));
    }
}
