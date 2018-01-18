package org.ebc.jwt.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ebc.jwt.security.model.AccountCredentials;
import org.ebc.jwt.security.service.TokenAuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.DigestUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * This class is responsible to login filter
 * @version 1.0
 * @author eduardobarbosa
 * @since 11/01/2018
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private String jwtSecretKey;

    public JWTLoginFilter(String jwtSecretKey, String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
        this.jwtSecretKey = jwtSecretKey;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        AccountCredentials credentials = new ObjectMapper()
                .readValue(request.getInputStream(), AccountCredentials.class);
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        credentials.getUsername(),
                        DigestUtils.md5DigestAsHex(credentials.getPassword().getBytes()),
                        Collections.emptyList()
                )
        );
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain,
            Authentication auth) throws IOException, ServletException {

        TokenAuthenticationService.addAuthentication(this.jwtSecretKey, response, auth);
    }

}