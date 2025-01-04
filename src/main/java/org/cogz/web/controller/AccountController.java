package org.cogz.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.cogz.web.dto.UserDto;
import org.cogz.web.model.User;
import org.cogz.web.service.IAccountService;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author altrax
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private IAccountService accountService;

    @GetMapping("/list-users")
    public List<UserDto> getUsers() {
        Mapper mapper = new DozerBeanMapper();
        List<UserDto> result = new ArrayList<>();
        for (User user : accountService.getUsers()) {
            result.add(mapper.map(user, UserDto.class));
        }
        logger.info("user list - {}", result.size());
        return result;
    }

    @GetMapping("/current-user")
    public UserDto getCurrentUser() {
        User result = accountService.getCurrentUser();
        return new DozerBeanMapper().map(result, UserDto.class);
    }

    @PostMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestBody UserDto userDto) throws JsonProcessingException {
        logger.info("add user - {}", new ObjectMapper().writeValueAsString(userDto));
        accountService.addUser(userDto);
        return ResponseEntity.ok("User added successfully.");
    }

    @PostMapping("/edit-user")
    public ResponseEntity<?> editUser(@RequestBody UserDto productDto) throws JsonProcessingException {
        logger.info("edit user - {}", new ObjectMapper().writeValueAsString(productDto));
        accountService.editUser(productDto);
        return ResponseEntity.ok("User edited successfully.");
    }

    @PostMapping("/deactivate-user")
    public ResponseEntity<?> deactivateUser(@RequestBody String username) {
        logger.info("delete user - {}", username);
        accountService.deactivateUser(username);
        return ResponseEntity.ok("User deleted successfully.");
    }
}
