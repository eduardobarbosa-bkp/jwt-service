package org.ebc.jwt.security.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ebc.jwt.security.service.TokenAuthenticationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * This class is responsible to token filter
 * @version 1.0
 * @author eduardobarbosa
 * @since 11/01/2018
 */
public class JWTAuthenticationFilter extends GenericFilterBean {

    private String jwtSecretKey;

    private Log log = LogFactory.getLog(JWTAuthenticationFilter.class);

    public JWTAuthenticationFilter(String jwtSecretKey) {
        this.jwtSecretKey = jwtSecretKey;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        Authentication authentication = null;
        try {
            authentication = TokenAuthenticationService
                    .getAuthentication(this.jwtSecretKey, (HttpServletRequest) request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

}
