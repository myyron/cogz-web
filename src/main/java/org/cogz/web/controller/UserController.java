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
import org.cogz.web.enums.EGameType;
import org.cogz.web.enums.EUserEditStatus;
import org.cogz.web.enums.EUserStatus;
import org.cogz.web.model.User;
import org.cogz.web.model.UserEdit;
import org.cogz.web.model.UserTask;
import org.cogz.web.service.IUserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
        return result;
    }

    @GetMapping("/current")
    public UserDto getCurrentUser() {
        User user = userService.getCurrentUser();
        UserDto userDto = new ModelMapper().map(user, UserDto.class);
        UserTask userTask = userService.getWaiver();
        if (userTask != null) {
            userDto.setWaiverAccepted(true);
            userDto.setWaiverAcceptedDate(userTask.getInsDate());
        }
        return userDto;
    }

    @GetMapping("/list-registration")
    public List<UserDto> getUsersForVerification() {
        ModelMapper mapper = new ModelMapper();
        List<UserDto> result = new ArrayList<>();
        for (User user : userService.getUsersForVerification()) {
            result.add(mapper.map(user, UserDto.class));
        }
        return result;
    }

    @GetMapping("/list-modification")
    public List<UserDto> getUsersEdit() {
        ModelMapper mapper = new ModelMapper();
        List<UserDto> result = new ArrayList<>();
        for (UserEdit userEdit : userService.getUsersEdit()) {
            User user = userService.getUser(userEdit.getUserId());
            UserDto userDto = mapper.map(user, UserDto.class);
            userDto.setUserEdit(mapper.map(userEdit, UserEditDto.class));
            result.add(userDto);
        }
        return result;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserWithPasswordDto userDto) throws JsonProcessingException {
        logger.info("create user - {}", new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(userDto));
        userService.createUser(userDto);
        return ResponseEntity.ok("User created successfully.");
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editUser(@RequestBody UserDto userDto) throws JsonProcessingException {
        logger.info("user-edit - {}", new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(userDto));
        userService.editUser(userDto);
        return ResponseEntity.ok("User edited successfully.");
    }

    @PostMapping("/profile-edit")
    public ResponseEntity<?> profileEdit(Integer id, String username, String firstname, String lastname, String callsign, String email,
            String mobileNumber, LocalDate birthdate) throws IOException {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setUsername(username);
        userDto.setFirstname(firstname);
        userDto.setLastname(lastname);
        userDto.setCallsign(callsign);
        userDto.setEmail(email);
        userDto.setMobileNumber(mobileNumber);
        userDto.setBirthdate(birthdate);

        logger.info("user-profile-edit - {}", new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(userDto));

        userService.editUserFromProfile(userDto);
        return ResponseEntity.ok("Profile edited successfully.");
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateUser(Integer userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.ok("User deactivated successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(Integer userId, String password) {
        userService.resetPassword(userId, password);
        return ResponseEntity.ok("User password reset successfully.");
    }

    @PostMapping("/create-useredit")
    public ResponseEntity<?> createUserEdit(MultipartFile validId, String username, String firstname, String lastname,
            String callsign, String email, String mobileNumber, LocalDate birthdate) throws IOException {
        UserEditDto userDto = new UserEditDto();
        userDto.setUsername(username);
        userDto.setFirstname(firstname);
        userDto.setLastname(lastname);
        userDto.setCallsign(callsign);
        userDto.setEmail(email);
        userDto.setMobileNumber(mobileNumber);
        userDto.setBirthdate(birthdate);
        userService.createUserEdit(validId, userDto);
        return ResponseEntity.ok("User edit created successfully.");
    }

    @PostMapping("/accept-waiver")
    public LocalDateTime acceptWaiver() {
        return userService.acceptWaiver().getInsDate();
    }

    @PostMapping("/change-pic")
    public ResponseEntity<?> changePicture(@RequestParam MultipartFile profilePic) throws IOException {
        userService.changePicture(profilePic);
        return ResponseEntity.ok("User profile picture changed successfully.");
    }

    @PostMapping("/reg-game")
    public ResponseEntity<?> registerGame(@RequestParam MultipartFile paymentProof, Integer gameId, LocalDate gameSchedule,
            EGameType gameType, Integer[] additionalPaxArray, String[] firstnameArray,
            String[] lastnameArray) throws IOException {
        userService.registerGame(paymentProof, gameId, gameSchedule, gameType, additionalPaxArray, firstnameArray, lastnameArray);
        return ResponseEntity.ok("User registered to game successfully.");
    }

    @PostMapping("/verification-good")
    public ResponseEntity<?> verificationGood(Integer userId) throws IOException {
        userService.changeStatus(userId, EUserStatus.GOOD);
        return ResponseEntity.ok("User verified to good successfully.");
    }

    @PostMapping("/verification-banned")
    public ResponseEntity<?> verificationBanned(Integer userId) throws IOException {
        userService.changeStatus(userId, EUserStatus.BANNED);
        return ResponseEntity.ok("User verified to banned successfully.");
    }

    @PostMapping("/modification-approve")
    public ResponseEntity<?> approveUserEdit(Integer userId) throws IOException {
        userService.changeUserEditStatus(userId, EUserEditStatus.APPROVED);
        return ResponseEntity.ok("User edit approved successfully.");
    }

    @PostMapping("/modification-reject")
    public ResponseEntity<?> rejectUserEdit(Integer userId) throws IOException {
        userService.changeUserEditStatus(userId, EUserEditStatus.REJECTED);
        return ResponseEntity.ok("User edit rejected successfully.");
    }
}
