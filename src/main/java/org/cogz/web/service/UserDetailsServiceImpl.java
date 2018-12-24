package org.cogz.web.service;

import org.cogz.web.UserPrincipal;
import org.cogz.web.entity.User;
import org.cogz.web.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author mlatorilla
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User result = userRepository.findByUsername(username);
        if (result == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserPrincipal(result);
    }

}
