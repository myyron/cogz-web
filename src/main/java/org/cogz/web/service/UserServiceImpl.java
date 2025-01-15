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
package org.cogz.web.service;

import java.time.LocalDateTime;
import java.util.List;
import org.cogz.web.dto.UserDto;
import org.cogz.web.model.User;
import org.cogz.web.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 *
 * @author altrax
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Integer createUser(UserDto userDto) {
        User user = new ModelMapper().map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (!StringUtils.hasLength(userDto.getInsBy())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            user.setInsBy(authentication.getName());
        }

        return userRepository.save(user).getId();
    }

    @Override
    @Transactional
    public void editUser(UserDto userDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<UserDto, User>() {
            @Override
            protected void configure() {
                skip(destination.getInsBy());
                skip(destination.getPassword());
            }
        });

        User user = userRepository.findByUsername(userDto.getUsername()).get();

        modelMapper.map(userDto, user);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        user.setUpdBy(authentication.getName());
        user.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deactivateUser(String username) {
        User user = userRepository.findByUsername(username).get();
        user.setEnabled(0);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        user.setUpdBy(authentication.getName());
        user.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void resetPassword(String username, String password) {
        User user = userRepository.findByUsername(username).get();
        user.setPassword(passwordEncoder.encode(password));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        user.setUpdBy(authentication.getName());
        user.setUpdDate(LocalDateTime.now());
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

    @Override
    public User getUser(Integer userId) {
        return userRepository.findByIdAndEnabled(userId, 1).get();
    }

}
