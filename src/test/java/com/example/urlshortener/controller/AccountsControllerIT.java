package com.example.urlshortener.controller;

import com.example.urlshortener.main.UrlShortenerApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.example.urlshortener.controller.AccountsController.AccountOpeningStatus.SUCCESSFUL;
import static com.example.urlshortener.controller.AccountsController.AccountOpeningStatus.UNSUCCESSFUL;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UrlShortenerApplication.class})
@WebAppConfiguration
public class AccountsControllerIT {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void openAccount() throws Exception {
        mockMvc
                .perform(post("/account")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"AccountId\":\"first\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.success").value(SUCCESSFUL.getStatus()))
                .andExpect(jsonPath("$.description").value(SUCCESSFUL.getDescription()))
                .andExpect(jsonPath("$.password").isNotEmpty());
    }

    @Test
    public void openAccount_contentTypeNotSpecified() throws Exception {
        mockMvc
                .perform(post("/account")
                        .content("{\"AccountId\":\"first\"}")
                )
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.errorCode").value(UNSUPPORTED_MEDIA_TYPE.value()))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void openAccount_emptyId() throws Exception {
        mockMvc
                .perform(post("/account")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"AccountId\":\"\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.success").value(SUCCESSFUL.getStatus()))
                .andExpect(jsonPath("$.description").value(SUCCESSFUL.getDescription()))
                .andExpect(jsonPath("$.password").isNotEmpty());
    }

    @Test
    public void openAccount_noContent() throws Exception {
        mockMvc
                .perform(post("/account")
                        .contentType(APPLICATION_JSON_UTF8)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.errorCode").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    public void openAccount_twoTimes() throws Exception {
        mockMvc
                .perform(post("/account")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"AccountId\":\"second\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.success").value(SUCCESSFUL.getStatus()))
                .andExpect(jsonPath("$.description").value(SUCCESSFUL.getDescription()))
                .andExpect(jsonPath("$.password").isNotEmpty());

        mockMvc
                .perform(post("/account")
                        .contentType(APPLICATION_JSON)
                        .content("{\"AccountId\":\"second\"}")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andExpect(jsonPath("$.success").value(UNSUCCESSFUL.getStatus()))
                .andExpect(jsonPath("$.description").value(UNSUCCESSFUL.getDescription()));
    }
}