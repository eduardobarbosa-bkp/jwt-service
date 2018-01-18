package org.ebc.jwt.security.repository;

import org.ebc.jwt.security.model.AccountCredentials;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This class is responsible to store the data using the data base
 * @version 1.0
 * @author eduardobarbosa
 * @since 11/01/2018
 */
public interface AuthRepository extends MongoRepository<AccountCredentials, String> {

    AccountCredentials findByUsername(String username);
}
