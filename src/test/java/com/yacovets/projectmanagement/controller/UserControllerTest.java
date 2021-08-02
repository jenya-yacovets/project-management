package com.yacovets.projectmanagement.controller;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    @BeforeAll
    public void getCookies() throws Exception {
        mockMvc.perform(
                post("/security/registration")
                        .accept(MediaType.TEXT_HTML)
                        .param("email", "Alex.email3@dev.ioc")
                        .param("username", "Alex3")
                        .param("password", "12345678")
                        .param("passwordConfirmation", "12345678")
                        .with(csrf())
        ).andExpect(redirectedUrl("/security/authentication")).andDo(mvcResult -> {

            MvcResult result = mockMvc.perform(
                    post("/security/authentication")
                            .accept(MediaType.TEXT_HTML)
                            .param("emailOrUsername", "Alex3")
                            .param("password", "12345678")
                            .with(csrf())
            ).andExpect(redirectedUrl("/home")).andReturn();

            this.session = (MockHttpSession) result.getRequest().getSession();
        });
    }

    @Test
    void userProfile() throws Exception {

        mockMvc.perform(
                get("/profile")
                        .session(this.session)
                        .accept(MediaType.TEXT_HTML)
        ).andExpect(status().isOk());
    }
}
