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

import java.net.URI;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
public class BoardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private MockHttpSession session;

    private String aliasProject;

    @BeforeAll
    public void getCookies() throws Exception {
        mockMvc.perform(
                post("/security/registration")
                        .accept(MediaType.TEXT_HTML)
                        .param("email", "Alex.email1@dev.ioc")
                        .param("username", "Alex1")
                        .param("password", "12345678")
                        .param("passwordConfirmation", "12345678")
                        .with(csrf())
        ).andExpect(redirectedUrl("/security/authentication")).andDo(mvcResult -> {

            MvcResult result = mockMvc.perform(
                    post("/security/authentication")
                            .accept(MediaType.TEXT_HTML)
                            .param("emailOrUsername", "Alex1")
                            .param("password", "12345678")
                            .with(csrf())
            ).andExpect(redirectedUrl("/home")).andReturn();

            this.session = (MockHttpSession) result.getRequest().getSession();
        });

        MvcResult mvcResult = mockMvc.perform(
                post("/project/create")
                        .session(this.session)
                        .param("alias", "testAlias")
                        .param("name", "Test name")
                        .param("description", "Test description")
                        .with(csrf())
                        .accept(MediaType.TEXT_HTML)
        ).andExpect(redirectedUrlPattern("/project/*")).andReturn();

        URI uri = new URI(mvcResult.getResponse().getRedirectedUrl());
        String path = uri.getPath();
        this.aliasProject = path.substring(path.lastIndexOf('/'));
    }

    @Test
    void createBoardProjectListView() throws Exception {
        mockMvc.perform(
                get("/board/create")
                        .accept(MediaType.TEXT_HTML)
                        .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void createBoardView() throws Exception {
        mockMvc.perform(
                get(String.format("/board/create/%s", aliasProject))
                        .accept(MediaType.TEXT_HTML)
                        .session(session)
        ).andExpect(status().isOk());
    }

    @Test
    void createBoardHandler() throws Exception {
        mockMvc.perform(
                post(String.format("/board/create/%s", aliasProject))
                        .session(this.session)
                        .param("name", "Test name")
                        .param("description", "Test description")
                        .with(csrf())
                        .accept(MediaType.TEXT_HTML)
        ).andExpect(redirectedUrlPattern("/board/*"));
    }

}
