package org.ebc.jwt.security.controller;

import org.ebc.jwt.security.dto.AccountCredentialsDTO;
import org.ebc.jwt.security.model.AccountCredentials;
import org.ebc.jwt.security.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * @author eduardobarbosa
 * @since 11/01/2018
 */
@RestController
public class AuthController {

    @Autowired
    private AuthService service;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@RequestBody AccountCredentials user){
        service.register(user);
    }

    @RequestMapping(value = "/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN_USER')")
    public Collection<AccountCredentialsDTO> list(){
        return service.list();
    }






}
