package org.ebc.jwt.security.service;

import org.ebc.jwt.security.dto.AccountCredentialsDTO;
import org.ebc.jwt.security.model.AccountCredentials;
import org.ebc.jwt.security.repository.AuthRepository;
import org.ebc.jwt.security.util.AccountSecurityConfig;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is responsible to provide the business logic for account credentials
 * @version 1.0
 * @author eduardobarbosa
 * @since 11/01/2018
 */
@Service
public class AuthService implements UserDetailsService {

   @Autowired
   private AuthRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        AccountCredentials credentials = this.repository.findByUsername(username);
        if(credentials != null){
           return new User(credentials.getUsername(), credentials.getPassword()
                    , credentials.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        }else{
            throw new UsernameNotFoundException("user not found!");
        }
    }

    public void register(AccountCredentials user) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setRoles(Collections.singletonList(AccountSecurityConfig.ROLES.ROLE_STANDARD_USER.toString()));
        this.repository.insert(user);
    }

    public Collection<AccountCredentialsDTO> list() {
        List<AccountCredentials> list = this.repository.findAll();
        return list.stream().map(ac -> {
                AccountCredentialsDTO dto = new AccountCredentialsDTO();
                BeanUtils.copyProperties(ac, dto);
                return dto;
                }).collect(Collectors.toList());
    }
}
