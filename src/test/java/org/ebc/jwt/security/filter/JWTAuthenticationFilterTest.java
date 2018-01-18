package org.ebc.jwt.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.ebc.jwt.security.model.AccountCredentials;
import org.ebc.jwt.security.service.TokenAuthenticationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

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
public class JWTAuthenticationFilterTest {

    public static final String URL_LOGIN = "/login";
    public static final String URL_USERS = "/users";

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Value("${jwt.admin.username}")
    private String username;

    @Value("${jwt.admin.password}")
    private String password;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void request_with_token() throws Exception {
        String token = performLogin();
        //given a valid token with user credentials ROLE_ADMIN_USER
        MockHttpServletRequestBuilder requestBuilder = get(URL_USERS)
                .header(TokenAuthenticationService.HEADER_STRING, TokenAuthenticationService.TOKEN_PREFIX + " " + token);
        //when perform a request to /users
        ResultActions perform = mockMvc.perform(requestBuilder);
        //then the return OK
        perform.andExpect(status().isOk());
    }

    @Test
    public void request_invalid_token() throws Exception {
        String token = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS512, TextCodec.BASE64.encode(this.jwtSecretKey + "_invalid".getBytes()))
                .compact();
        //given an invalid token
        MockHttpServletRequestBuilder requestBuilder = get(URL_USERS)
                .header(TokenAuthenticationService.HEADER_STRING, TokenAuthenticationService.TOKEN_PREFIX + " " + token);
        //when perform a request to /users
        ResultActions perform = mockMvc.perform(requestBuilder);
        //then the return Unauthorized
        perform.andExpect(status().is4xxClientError());
    }

    @Test
    public void request_without_token() throws Exception {
        //given no token
        MockHttpServletRequestBuilder requestBuilder = get(URL_USERS);
        //when perform a request to /users
        ResultActions perform = mockMvc.perform(requestBuilder);
        //then the return Unauthorized
        perform.andExpect(status().is4xxClientError());
    }

    private String performLogin() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        AccountCredentials credentials = new AccountCredentials(this.username, this.password, Collections.<String>emptyList());

        MockHttpServletRequestBuilder requestBuilder = post(URL_LOGIN)
                .content(objectMapper.writeValueAsString(credentials));

        ResultActions perform = mockMvc.perform(requestBuilder);
        perform.andExpect(status().isOk());
        String contentAsString = perform.andReturn().getResponse().getContentAsString();
        Map mapJson = objectMapper.readValue(contentAsString, Map.class);
        return mapJson.get(TokenAuthenticationService.KEY_TOKEN).toString();
    }


}
