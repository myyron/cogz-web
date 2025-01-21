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

import org.antlr.v4.runtime.misc.LogManager;
import org.cogz.web.controller.UserController;
import org.cogz.web.dto.UserDto;
import org.cogz.web.dto.UserEditDto;
import org.cogz.web.dto.UserWithPasswordDto;
import org.cogz.web.enums.ETaskType;
import org.cogz.web.model.User;
import org.cogz.web.model.UserEdit;
import org.cogz.web.model.UserTask;
import org.cogz.web.repository.UserEditRepository;
import org.cogz.web.repository.UserRepository;
import org.cogz.web.repository.UserTaskRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author altrax
 */
@Service
public class UserServiceImpl extends BaseService implements IUserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEditRepository userEditRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Override
    @Transactional
    public Integer createUser(UserWithPasswordDto userDto) {
        User user = new ModelMapper().map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (userDto.getInsBy() == null) {
            user.setInsBy(sessionInfo.getCurrentUser().getId());
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

        user.setUpdBy(sessionInfo.getCurrentUser().getId());
        user.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deactivateUser(String username) {
        User user = userRepository.findByUsername(username).get();
        user.setEnabled(0);
        user.setUpdBy(sessionInfo.getCurrentUser().getId());
        user.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void resetPassword(String username, String password) {
        User user = userRepository.findByUsername(username).get();
        user.setPassword(passwordEncoder.encode(password));
        user.setUpdBy(sessionInfo.getCurrentUser().getId());
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

    @Override
    @Transactional
    public Integer createUserEdit(UserEditDto userEditDto) {
        UserEdit userEdit = new ModelMapper().map(userEditDto, UserEdit.class);
        userEdit.setUserId(sessionInfo.getCurrentUser().getId());
        userEdit.setInsBy(sessionInfo.getCurrentUser().getId());
        return userEditRepository.save(userEdit).getId();
    }

    @Override
    @Transactional
    public void approveUserEdit(Integer userEditId) {

        UserEdit userEdit = userEditRepository.findByIdAndEnabled(userEditId, 1).get();
        User user = userRepository.findByIdAndEnabled(userEdit.getUserId(), 1).get();

        user.setUsername(userEdit.getUsername());
        user.setFirstname(userEdit.getFirstname());
        user.setLastname(userEdit.getLastname());
        user.setEmail(userEdit.getEmail());
        user.setMobileNumber(userEdit.getMobileNumber());
        user.setBirthdate(userEdit.getBirthdate());
        user.setUpdBy(sessionInfo.getCurrentUser().getId());
        user.setUpdDate(LocalDateTime.now());

        userEdit.setEnabled(0);
        userEdit.setUpdBy(sessionInfo.getCurrentUser().getId());
        userEdit.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void acceptWaiver() {
        UserTask userTask = new UserTask();
        userTask.setUserId(sessionInfo.getCurrentUser().getId());
        userTask.setType(ETaskType.WAIVER);
        userTask.setStatus("ACCEPTED");
        userTask.setInsBy(sessionInfo.getCurrentUser().getId());
        userTaskRepository.save(userTask);
        logger.info("waiver accepted - {}", sessionInfo.getCurrentUser().getUsername());
    }

    @Override
    public Boolean isWaiverAccepted() {
        return userTaskRepository.existsByUserIdAndTypeAndEnabled(sessionInfo.getCurrentUser().getId(), ETaskType.WAIVER, 1);
    }
}
