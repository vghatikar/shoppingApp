package com.publicis.ecommerce.dao;

import com.publicis.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for {@link User} entity
 *
 */

@RepositoryRestResource
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a User through the given username.
     *
     * @param username the username to look for
     * @return the User that was found (if any)
     */
    Optional<User> findByUsername(String username);

}
