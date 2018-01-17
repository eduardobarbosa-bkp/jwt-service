package org.ebc.jwt;

import org.ebc.jwt.security.model.AccountCredentials;
import org.ebc.jwt.security.repository.AuthRepository;
import org.ebc.jwt.security.util.AccountSecurityConfig;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.DigestUtils;

import java.util.Arrays;

/**
 * @author eduardobarbosa
 * @since 11/01/2018
 */
@SpringBootApplication
@EnableAutoConfiguration
public class JwtServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(JwtServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			String admUsername = ctx.getEnvironment().getProperty("jwt.admin.username");
			String admPassword = ctx.getEnvironment().getProperty("jwt.admin.password");
			AuthRepository repository = ctx.getBean(AuthRepository.class);
			AccountCredentials admin = new AccountCredentials();
			admin.setUsername(admUsername);
			admin.setPassword(DigestUtils.md5DigestAsHex(admPassword.getBytes()));
			admin.setRoles(Arrays.asList(AccountSecurityConfig.ROLES.ROLE_ADMIN_USER.toString()));
			repository.save(admin);
		};
	}
}
