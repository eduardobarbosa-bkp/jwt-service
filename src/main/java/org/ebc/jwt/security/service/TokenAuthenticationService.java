package org.ebc.jwt.security.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author eduardobarbosa
 * @since 11/01/2018
 */
public class TokenAuthenticationService {

    public static final String TOKEN_PREFIX = "Bearer";
    public static final String HEADER_STRING = "Authorization";
    public static final String ISSUER = "org.ebc.jwt";
    public static final int EXPIRATION_TIME_IN_MINUTES = 15;
    public static final String CLAIM_ROLES = "roles";
    public static final String KEY_TOKEN = "token";

    public static void addAuthentication(String jwtSecretKey, HttpServletResponse response, Authentication authentication) throws IOException {

        String token = buildToken(jwtSecretKey, authentication.getName(), authentication.getAuthorities());

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(KEY_TOKEN, token);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getWriter(), tokenMap);
    }

    private static String buildToken(String jwtSecretKey, String username, Collection<? extends GrantedAuthority> authorities) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(CLAIM_ROLES, authorities.stream().map(Object::toString).collect(Collectors.toList()));
        Instant now = Instant.now(Clock.systemUTC());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(ISSUER)
                .setId(UUID.randomUUID().toString())
                .setNotBefore(Date.from(now))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(EXPIRATION_TIME_IN_MINUTES, ChronoUnit.MINUTES)))
                .signWith(SignatureAlgorithm.HS512, TextCodec.BASE64.encode(jwtSecretKey.getBytes()))
                .compact();
    }

    public static Authentication getAuthentication(String jwtSecretKey, HttpServletRequest request) throws Exception{
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            Claims claims = Jwts.parser()
                    .setSigningKey(TextCodec.BASE64.encode(jwtSecretKey.getBytes()))
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
            String user = claims.getSubject();
            Collection<String> scopes = claims.get(CLAIM_ROLES, Collection.class);
            List<SimpleGrantedAuthority> authorities = scopes.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, authorities);
            }
        }
        return null;
    }

}
