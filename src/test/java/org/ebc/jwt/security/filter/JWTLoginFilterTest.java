package org.ebc.jwt.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ebc.jwt.security.model.AccountCredentials;
import org.ebc.jwt.security.service.TokenAuthenticationService;
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

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author eduardobarbosa
 * @since 11/01/2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class JWTLoginFilterTest {

    public static final String URL_LOGIN = "/login";
    public static final String INVALID_PASSWORD = "test123";

    @Value("${jwt.admin.username}")
    private String username;

    @Value("${jwt.admin.password}")
    private String password;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void login_success() throws Exception {
        AccountCredentials credentials = new AccountCredentials(this.username, this.password, Collections.<String>emptyList());
        MockHttpServletRequestBuilder requestBuilder = post(URL_LOGIN)
                .content(objectMapper.writeValueAsString(credentials))
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions perform = mockMvc.perform(requestBuilder);
        perform.andExpect(status().isOk());
        String contentAsString = perform.andReturn().getResponse().getContentAsString();
        Map mapJson = objectMapper.readValue(contentAsString, Map.class);
        assertThat(mapJson.get(TokenAuthenticationService.KEY_TOKEN)).isNotNull();
    }

    @Test
    public void login_fail() throws Exception {
        AccountCredentials credentials = new AccountCredentials(this.username, INVALID_PASSWORD, Collections.<String>emptyList());
        MockHttpServletRequestBuilder requestBuilder = post(URL_LOGIN)
                .content(objectMapper.writeValueAsString(credentials));

        ResultActions perform = mockMvc.perform(requestBuilder);
        perform.andExpect(status().isUnauthorized());
    }


    @Test
    public void login_user_not_found() throws Exception {
        AccountCredentials credentials = new AccountCredentials(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Collections.<String>emptyList());
        MockHttpServletRequestBuilder requestBuilder = post(URL_LOGIN)
                .content(objectMapper.writeValueAsString(credentials));
        ResultActions perform = mockMvc.perform(requestBuilder);
        perform.andExpect(status().isUnauthorized());
    }


}
