package org.ebc.jwt.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ebc.jwt.security.model.AccountCredentials;
import org.ebc.jwt.security.repository.AuthRepository;
import org.ebc.jwt.security.service.TokenAuthenticationService;
import org.ebc.jwt.security.util.AccountSecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.DigestUtils;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author eduardobarbosa
 * @since 11/01/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    public static final String URL_LOGIN = "/login";
    public static final String URL_REGISTER = "/register";
    public static final String URL_USERS = "/users";
    public static final String NEW_USERNAME = UUID.randomUUID().toString();
    public static final String NEW_PASSWORD = UUID.randomUUID().toString();
    public static final String NEW_PASSWORD_MD5 = DigestUtils.md5DigestAsHex(NEW_PASSWORD.getBytes());

    @Autowired
    private MockMvc mockMvc;

    @Value("${jwt.admin.username}")
    private String username;

    @Value("${jwt.admin.password}")
    private String password;

    @Autowired
    private AuthRepository repository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void register_user_success() throws Exception {
        AccountCredentials newUser = new AccountCredentials(NEW_USERNAME, NEW_PASSWORD, Collections.<String>emptyList());
        MockHttpServletRequestBuilder requestBuilder = post(URL_REGISTER)
                .content(objectMapper.writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions perform = mockMvc.perform(requestBuilder);
        perform.andExpect(status().isOk());
        AccountCredentials accountCredentials = repository.findByUsername(NEW_USERNAME);
        assertThat(accountCredentials).isNotNull();
        assertThat(accountCredentials.getUsername()).isEqualTo(NEW_USERNAME);
        assertThat(accountCredentials.getPassword()).isEqualTo(NEW_PASSWORD_MD5);
    }


    @Test(expected = Exception.class)
    public void register_duplicated_user() throws Exception {
        AccountCredentials newUser = new AccountCredentials(this.username, this.password, Collections.<String>emptyList());
        MockHttpServletRequestBuilder requestBuilder = post(URL_REGISTER)
                .content(objectMapper.writeValueAsString(newUser))
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder);
    }


    @Test
    public void find_users_role_admin() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = get(URL_USERS);
        setAuthHeader(requestBuilder, this.username, this.password);

        ResultActions perform = mockMvc.perform(requestBuilder);
        perform.andExpect(status().isOk());
    }

    @Test
    public void find_users_standard_user() throws Exception {

        String username = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();

        persistUser(username, password);

        MockHttpServletRequestBuilder requestBuilder = get(URL_USERS);
        setAuthHeader(requestBuilder, username, password);

        ResultActions perform = mockMvc.perform(requestBuilder);
        perform.andExpect(status().is4xxClientError());

    }

    private AccountCredentials persistUser(String username, String password) {
        AccountCredentials credentials = new AccountCredentials(username, DigestUtils.md5DigestAsHex(password.getBytes()), Collections.singleton(AccountSecurityConfig.ROLES.ROLE_STANDARD_USER.toString()));
        this.repository.insert(credentials);
        return credentials;
    }


    private void setAuthHeader(MockHttpServletRequestBuilder requestBuilder, String username, String password) throws Exception{
        String token = getToken(username, password);
        requestBuilder.header(TokenAuthenticationService.HEADER_STRING, TokenAuthenticationService.TOKEN_PREFIX + " " + token);
    }

    private String getToken(String username, String password) throws Exception {

        AccountCredentials credentials = new AccountCredentials(username, password, Collections.<String>emptyList());
        MockHttpServletRequestBuilder requestBuilder = post(URL_LOGIN)
                .content(objectMapper.writeValueAsString(credentials));

        ResultActions perform = mockMvc.perform(requestBuilder);
        perform.andExpect(status().isOk());
        String contentAsString = perform.andReturn().getResponse().getContentAsString();
        Map mapJson = objectMapper.readValue(contentAsString, Map.class);
        return  (String) mapJson.get(TokenAuthenticationService.KEY_TOKEN);
    }

}
