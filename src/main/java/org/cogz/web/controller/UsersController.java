/*
 * Copyright 2018 Myyron Latorilla
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
import java.io.IOException;
import org.cogz.web.dto.UserDto;
import org.cogz.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The users controller class.
 *
 * @author Myyron Latorilla
 */
@Controller
@RequestMapping("/settings/users")
public class UsersController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String showUsersPage() {
        return "settings/users";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public String getUserList() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(userService.getAllUsers(true));
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public Long createUser(@RequestParam String userDto) throws IOException {
        return userService.createUser(new ObjectMapper().readValue(userDto, UserDto.class));
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public Long editUser(@RequestParam String userDto) throws IOException {
        return userService.editUser(new ObjectMapper().readValue(userDto, UserDto.class));
    }

    @RequestMapping(value = "/resetpw", method = RequestMethod.POST)
    @ResponseBody
    public Long resetPassword(@RequestParam String userDto) throws IOException {
        return userService.resetPassword(new ObjectMapper().readValue(userDto, UserDto.class));
    }
}
