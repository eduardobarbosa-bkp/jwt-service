package org.ebc.jwt.security.model;


import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @version 1.0
 * @author eduardobarbosa
 * @since 11/01/2018
 */
public class AccountCredentials implements Serializable {

    private static final long serialVersionUID = -5996517299378591591L;
    @Id
    private String username;
    private String password;
    private Collection<String> roles = new ArrayList<>();

    public AccountCredentials() {
    }

    public AccountCredentials(String username, String password, Collection<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = roles;
    }

    public Collection<String> getRoles() {
        return roles;
    }

    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}