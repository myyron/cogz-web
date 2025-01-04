package org.cogz.web.service;

import java.util.List;
import org.cogz.web.dto.UserDto;
import org.cogz.web.model.User;

/**
 *
 * @author altrax
 */
public interface IAccountService {

    List<User> getUsers();
    
    User getCurrentUser();

    Integer addUser(UserDto userDto);

    void editUser(UserDto userDto);

    void deactivateUser(String username);
}
