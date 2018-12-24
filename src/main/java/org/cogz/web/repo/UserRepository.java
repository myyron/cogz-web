package org.cogz.web.repo;

import org.cogz.web.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author mlatorilla
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);
}
