package org.ebc.jwt.security.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author eduardobarbosa
 * @since 11/01/2018
 */
public class AccountCredentialsDTO implements Serializable {

    private static final long serialVersionUID = 5817548808143298691L;
    private String username;
    private Collection<String> roles = new ArrayList<>();

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
}
