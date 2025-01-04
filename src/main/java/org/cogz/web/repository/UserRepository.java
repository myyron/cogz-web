package org.cogz.web.repository;

import java.util.List;
import java.util.Optional;
import org.cogz.web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author altrax
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    List<User> findAllByEnabled(Integer enabled);

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);
}
