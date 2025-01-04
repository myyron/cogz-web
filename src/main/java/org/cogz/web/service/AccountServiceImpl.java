package org.cogz.web.service;

import java.util.List;
import org.cogz.web.dto.UserDto;
import org.cogz.web.model.User;
import org.cogz.web.repository.UserRepository;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author altrax
 */
@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Integer addUser(UserDto userDto) {
        User user = new DozerBeanMapper().map(userDto, User.class);
        Integer userId = userRepository.save(user).getId();
        return userId;
    }

    @Override
    @Transactional
    public void editUser(UserDto userDto) {
        userRepository.findByUsername(userDto.getUsername());
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public void deactivateUser(String username) {
        User user = userRepository.findByUsername(username).get();
        user.setEnabled(0);
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAllByEnabled(1);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(authentication.getName()).get();
    }

}
