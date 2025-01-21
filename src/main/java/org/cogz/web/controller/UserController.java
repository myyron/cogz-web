/*
 * Copyright 2025 Contractors of Ground Zero (CoGZ)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cogz.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.cogz.web.dto.UserDto;
import org.cogz.web.dto.UserEditDto;
import org.cogz.web.dto.UserWithPasswordDto;
import org.cogz.web.model.User;
import org.cogz.web.service.IUserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author altrax
 */
@RestController
@RequestMapping("/user")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public List<UserDto> getUsers() {
        ModelMapper mapper = new ModelMapper();
        List<UserDto> result = new ArrayList<>();
        for (User user : userService.getUsers()) {
            result.add(mapper.map(user, UserDto.class));
        }
        logger.info("user list - {}", result.size());
        return result;
    }

    @GetMapping("/current")
    public UserDto getCurrentUser() {
        User user = userService.getCurrentUser();
        UserDto userDto = new ModelMapper().map(user, UserDto.class);
        userDto.setWaiverAccepted(userService.isWaiverAccepted());
        return userDto;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserWithPasswordDto userDto) throws JsonProcessingException {
        logger.info("create user - {}", new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(userDto));
        userService.createUser(userDto);
        return ResponseEntity.ok("User created successfully.");
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editUser(@RequestBody UserDto userDto) throws JsonProcessingException {
        logger.info("edit user - {}", new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(userDto));
        userService.editUser(userDto);
        return ResponseEntity.ok("User edited successfully.");
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateUser(@RequestParam String username) {
        logger.info("delete user - {}", username);
        userService.deactivateUser(username);
        return ResponseEntity.ok("User deleted successfully.");
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestParam String username, @RequestParam String password) {
        logger.info("reset password - {}", username);
        userService.resetPassword(username, password);
        return ResponseEntity.ok("User password reset successfully.");
    }

    @PostMapping("/create-useredit")
    public ResponseEntity<?> createUserEdit(@RequestBody UserEditDto userEditDto) throws JsonProcessingException {
        logger.info("create user edit - {}", new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(userEditDto));
        userService.createUserEdit(userEditDto);
        return ResponseEntity.ok("User edit created successfully.");
    }

    @PostMapping("/accept-waiver")
    public ResponseEntity<?> acceptWaiver() throws JsonProcessingException {
        userService.acceptWaiver();
        return ResponseEntity.ok("Waiver accepted successfully.");
    }
}
