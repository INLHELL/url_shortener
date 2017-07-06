package com.example.urlshortener.controller;

import com.example.urlshortener.dao.AccountRepository;
import com.example.urlshortener.main.UrlShortenerApplication;
import com.example.urlshortener.model.Account;
import com.example.urlshortener.model.RegisteredUrl;
import com.example.urlshortener.service.AccountService;
import com.example.urlshortener.service.AccountStatisticsService;
import com.example.urlshortener.service.UrlRedirectService;
import com.example.urlshortener.service.UrlRegistrationService;
import com.google.common.collect.ImmutableMap;
import lombok.val;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static com.example.urlshortener.controller.AccountsController.AccountOpeningStatus.SUCCESSFUL;
import static java.util.Optional.of;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {UrlShortenerApplication.class})
@WebAppConfiguration
public class DocumentationTest {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    private RestDocumentationResultHandler documentationHandler;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @MockBean
    AccountService accountService;

    @MockBean
    UrlRegistrationService urlRegistrationService;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private UrlRedirectService urlRedirectService;

    @MockBean
    private AccountStatisticsService accountStatisticsService;


    @Before
    public void setup() {
        this.documentationHandler = document("{method-name}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.wac)
                .apply(springSecurity())
                .apply(documentationConfiguration(this.restDocumentation))
                .alwaysDo(documentationHandler)
                .build();
    }

    @Test
    public void account() throws Exception {
        when(accountService.openAccount(eq("first"))).thenReturn(of(new Account("first")));

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
                .andExpect(jsonPath("$.password").isNotEmpty())
                .andDo(document("{class-name}/{method-name}",
                        requestFields(
                                fieldWithPath("AccountId").type(STRING).description("Account identifier")),
                        responseFields(
                                fieldWithPath("success").description("Opening status").type(STRING),
                                fieldWithPath("description").description("Description of opening status").type(STRING),
                                fieldWithPath("password").description("Automatically generated password length of 8 alphanumeric characters").type(STRING).optional()
                        )));
    }

    @Test
    public void registerUrl() throws Exception {
        when(urlRegistrationService.register(anyString(), anyString(), anyInt())).thenReturn("HASH1234");
        Account account = new Account("username");
        when(accountRepository.findById(anyString())).thenReturn(of(account));
        mockMvc
                .perform(post("/register")
                        .with(httpBasic(account.getId(), account.getPassword()))
                        .contentType(APPLICATION_JSON_UTF8)
                        .content("{\"url\":\"http://yandex.ru\", \"redirectType\":301}")

                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(1)))
                .andExpect(jsonPath("$.shortUrl").value("http://localhost/HASH1234"))
                .andDo(document("{class-name}/{method-name}",
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Basic auth credentials")),
                        requestFields(
                                fieldWithPath("url").description("URL that needs shortening").type(STRING),
                                fieldWithPath("redirectType").description("Redirect type  301 | 302 (not mandatory, default 302)").optional().type(NUMBER)),
                        responseFields(
                                fieldWithPath("shortUrl").description("shortened URL").type(STRING)
                        )));
    }

    @Test
    public void redirectToUrl() throws Exception {
        String urlHash = "HASH1234";
        String expectedUrl = "http://yandex.ru";
        when(urlRedirectService.findRegisteredUrl(eq(urlHash))).thenReturn(of(
                RegisteredUrl
                        .builder()
                        .sourceUrl(expectedUrl)
                        .redirectType(302)
                        .build()));
        mockMvc
                .perform(get("/{urlHash}", urlHash))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(expectedUrl))//;
                .andDo(document("{class-name}/{method-name}",
                        pathParameters(
                                parameterWithName("urlHash").description("Registered URL hash")),
                        responseHeaders(
                                headerWithName(LOCATION).description("Refers to the target URL")
                        )
                ));
    }

    @Test
    public void statistics() throws Exception {
        val accountId = "accountId";
        val account = new Account(accountId);
        String url1 = "http://yandex.ru";
        int url1Uses = 123;
        String url2 = "http://google.com";
        int url2Uses = 456;
        String url3 = "http://mail.ru";
        int url3Uses = 789;
        when(accountStatisticsService.retrieveStatistics(eq(accountId))).thenReturn(ImmutableMap.of(
                url1, url1Uses,
                url2, url2Uses,
                url3, url3Uses));
        when(accountRepository.findById(eq(accountId))).thenReturn(of(account));
        mockMvc
                .perform(get("/statistics/{accountId}", accountId)
                        .with(httpBasic(account.getId(), account.getPassword()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.['" + url1 + "']").value(url1Uses))
                .andExpect(jsonPath("$.['" + url2 + "']").value(url2Uses))
                .andExpect(jsonPath("$.['" + url3 + "']").value(url3Uses))
                .andDo(document("{class-name}/{method-name}",
                        pathParameters(
                                parameterWithName("accountId").description("Account identifier")),
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("Basic auth credentials"))
                        ,
                        responseFields(
                                fieldWithPath("['http://yandex.ru']").description("Registered URL for redirection with usage counter").type(NUMBER),
                                fieldWithPath("['http://google.com']").description("Registered URL for redirection with usage counter").type(NUMBER),
                                fieldWithPath("['http://mail.ru']").description("Registered URL for redirection with usage counter").type(NUMBER)
                        )
                ));
    }
}
