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

import jakarta.validation.Valid;
import org.cogz.web.dto.UserWithPasswordDto;
import org.cogz.web.enums.ERole;
import org.cogz.web.enums.EUserStatus;
import org.cogz.web.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

/**
 * @author altrax
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private IUserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody UserWithPasswordDto userDto) {
        userDto.setRole(ERole.ROLE_USER);
        userDto.setStatus(EUserStatus.ACCOUNT_VERIFICATION);
        userDto.setInsBy(0);
        userService.createUser(userDto);
        return ResponseEntity.ok("User signup successfully.");
    }

    @PostMapping("/signup-valid-id")
    public Integer signupValidId(MultipartFile validId, String username, String firstname, String lastname,
            String callsign, String email, String mobileNumber, LocalDate birthdate, String password) throws IOException {

        UserWithPasswordDto userDto = new UserWithPasswordDto();
        userDto.setUsername(username);
        userDto.setPassword(password);
        userDto.setFirstname(firstname);
        userDto.setLastname(lastname);
        userDto.setCallsign(callsign);
        userDto.setEmail(email);
        userDto.setMobileNumber(mobileNumber);
        userDto.setBirthdate(birthdate);

        userDto.setRole(ERole.ROLE_USER);
        userDto.setStatus(EUserStatus.ACCOUNT_VERIFICATION);
        userDto.setInsBy(0);
        return userService.createUser(validId, userDto);
    }
}
