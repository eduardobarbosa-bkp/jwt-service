package org.ebc.jwt.security;

import org.ebc.jwt.security.filter.JWTAuthenticationFilter;
import org.ebc.jwt.security.filter.JWTCorsFilter;
import org.ebc.jwt.security.filter.JWTLoginFilter;
import org.ebc.jwt.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * This class is responsible to setup the security options
 * @version 1.0
 * @author eduardobarbosa
 * @since 11/01/2018
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String AUTHENTICATION_URL = "/login";
    public static final String REGISTER_URL = "/register";
    public static final String API_ROOT_URL = "/**";

    @Value("${jwt.secret.key}")
    private String jwtSecretKey;

    @Autowired
    private AuthService authService;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf().disable()
                .requestMatchers()
                .and()
                .authorizeRequests()
                .antMatchers(AUTHENTICATION_URL).permitAll()
                .antMatchers(REGISTER_URL).permitAll()
                .antMatchers(API_ROOT_URL).authenticated() // Protected API End-points
                .and()
                .addFilterBefore(new JWTCorsFilter(), ChannelProcessingFilter.class)
                .addFilterBefore(new JWTLoginFilter(jwtSecretKey, AUTHENTICATION_URL, authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthenticationFilter(jwtSecretKey),
                        UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authService);
    }
}
