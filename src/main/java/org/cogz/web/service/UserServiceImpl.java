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

import org.cogz.web.dto.UserDto;
import org.cogz.web.dto.UserEditDto;
import org.cogz.web.dto.UserWithPasswordDto;
import org.cogz.web.enums.EGameType;
import org.cogz.web.enums.ERegistrationStatus;
import org.cogz.web.enums.ETaskType;
import org.cogz.web.enums.EUserEditStatus;
import org.cogz.web.enums.EUserStatus;
import org.cogz.web.model.*;
import org.cogz.web.repository.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author altrax
 */
@Service
public class UserServiceImpl extends BaseService implements IUserService {

    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserEditRepository userEditRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private UserPaymentRepository userPaymentRepository;

    @Autowired
    private UserCompanionRepository userCompanionRepository;

    @Autowired
    private GameUserRepository gameUserRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private IFileService fileService;

    @Autowired
    private IMailService mailService;

    @Override
    @Transactional
    public void createUser(UserWithPasswordDto userDto) {
        User user = new ModelMapper().map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (userDto.getInsBy() == null) {
            user.setInsBy(sessionInfo.getCurrentUser().getId());
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void createUser(MultipartFile validId, UserWithPasswordDto userDto) throws IOException {
        User user = new ModelMapper().map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User savedUser = userRepository.save(user);
        mailService.accountRegistration(savedUser);
        fileService.writeImage(validId, "data/images/id/", savedUser.getId(), null, 400, false);
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

        User user = userRepository.findByIdAndEnabled(userDto.getId(), 1);

        if (!user.getUsername().equals(userDto.getUsername())) {
            expireUserSession(user.getUsername());
        }

        modelMapper.map(userDto, user);

        user.setUpdBy(sessionInfo.getCurrentUser().getId());
        user.setUpdDate(LocalDateTime.now());
    }

    @Transactional
    @Override
    public void editUserFromProfile(UserDto userDto) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new PropertyMap<UserDto, User>() {
            @Override
            protected void configure() {
                skip(destination.getRole());
                skip(destination.getStatus());
                skip(destination.getInsBy());
                skip(destination.getPassword());
            }
        });

        User user = userRepository.findByIdAndEnabled(userDto.getId(), 1);

        if (!user.getUsername().equals(userDto.getUsername())) {
            expireUserSession(user.getUsername());
            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        }

        modelMapper.map(userDto, user);

        user.setUpdBy(userDto.getId());
        user.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deactivateUser(Integer userId) {
        User user = userRepository.findByIdAndEnabled(userId, 1);
        user.setEnabled(0);
        user.setUpdBy(sessionInfo.getCurrentUser().getId());
        user.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void resetPassword(Integer userId, String password) {
        User user = userRepository.findByIdAndEnabled(userId, 1);
        user.setPassword(passwordEncoder.encode(password));
        user.setUpdBy(sessionInfo.getCurrentUser().getId());
        user.setUpdDate(LocalDateTime.now());
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAllByEnabled(1);
    }

    @Override
    public List<User> getUsersStrict() {
        return userRepository.findAllByStatusAndEnabled(EUserStatus.GOOD, 1);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsernameAndEnabled(authentication.getName(), 1).get();
    }

    @Override
    public User getUser(Integer userId) {
        return userRepository.findByIdAndEnabled(userId, 1);
    }

    @Override
    @Transactional
    public void createUserEdit(MultipartFile validId, UserEditDto userEditDto) throws IOException {
        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(new PropertyMap<UserEditDto, UserEdit>() {
            @Override
            protected void configure() {
                skip(destination.getId());
            }
        });
        UserEdit userEdit = userEditRepository.findByUserIdAndEnabled(sessionInfo.getCurrentUser().getId(), 1);
        if (userEdit == null) {
            userEdit = mapper.map(userEditDto, UserEdit.class);
            userEdit.setUserId(sessionInfo.getCurrentUser().getId());
            userEdit.setInsBy(sessionInfo.getCurrentUser().getId());
            userEditRepository.save(userEdit);
        } else {
            mapper.map(userEditDto, userEdit);
            userEdit.setUpdBy(sessionInfo.getCurrentUser().getId());
            userEdit.setUpdDate(LocalDateTime.now());
        }
        fileService.writeImage(validId, "data/images/id-edit/", sessionInfo.getCurrentUser().getId(), null, 400, false);
    }

    @Override
    @Transactional
    public void changeUserEditStatus(Integer userId, EUserEditStatus status) throws IOException {

        UserEdit userEdit = userEditRepository.findByUserIdAndEnabled(userId, 1);
        User user = userRepository.findByIdAndEnabled(userId, 1);

        if (status == EUserEditStatus.APPROVED) {

            if (!user.getUsername().equals(userEdit.getUsername())) {
                expireUserSession(user.getUsername());
                user.setUsername(userEdit.getUsername());
            }

            user.setFirstname(userEdit.getFirstname());
            user.setLastname(userEdit.getLastname());
            user.setCallsign(userEdit.getCallsign());
            user.setEmail(userEdit.getEmail());
            user.setMobileNumber(userEdit.getMobileNumber());
            user.setBirthdate(userEdit.getBirthdate());
            user.setUpdBy(sessionInfo.getCurrentUser().getId());
            user.setUpdDate(LocalDateTime.now());

            mailService.accountModificationApproved(user);
        } else {
            mailService.accountModificationRejected(user);
        }

        fileService.deleteImage("data/images/id-edit/", userId);

        userEdit.setStatus(status);
        userEdit.setEnabled(0);
        userEdit.setUpdBy(sessionInfo.getCurrentUser().getId());
        userEdit.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public UserTask acceptWaiver() {
        UserTask userTask = new UserTask();
        userTask.setUserId(sessionInfo.getCurrentUser().getId());
        userTask.setType(ETaskType.WAIVER);
        userTask.setStatus("ACCEPTED");
        userTask.setInsBy(sessionInfo.getCurrentUser().getId());
        userTaskRepository.save(userTask);
        logger.info("waiver accepted - {}", sessionInfo.getCurrentUser().getUsername());
        return userTask;
    }

    @Override
    @Transactional
    public void changeStatus(Integer userId, EUserStatus status) throws IOException {
        User user = userRepository.findByIdAndEnabled(userId, 1);
        user.setStatus(status);
        user.setUpdBy(sessionInfo.getCurrentUser().getId());
        user.setUpdDate(LocalDateTime.now());

        if (status == EUserStatus.GOOD) {
            mailService.accountRegistrationGood(user);
        } else if (status == EUserStatus.BANNED) {
            mailService.accountRegistrationBanned(user);
        }

        fileService.deleteImage("data/images/id/", userId);
    }

    @Override
    public UserTask getWaiver() {
        return userTaskRepository.findByUserIdAndTypeAndEnabled(sessionInfo.getCurrentUser().getId(), ETaskType.WAIVER, 1);
    }

    @Override
    public Boolean isWaiverAccepted(Integer userId) {
        return userTaskRepository.existsByUserIdAndTypeAndEnabled(userId, ETaskType.WAIVER, 1);
    }

    @Override
    public void changePicture(MultipartFile profilePic) throws IOException {
        fileService.writeImage(profilePic, "data/images/profile/", sessionInfo.getCurrentUser().getId(), null, 400, true);
    }

    @Override
    @Transactional
    public void registerGame(MultipartFile paymentProof, Integer gameId, LocalDate gameSchedule, EGameType gameType,
            Integer[] additionalPaxArray, String[] firstnameArray,
            String[] lastnameArray) throws IOException {

        Integer gameUserId = 0;

        if (gameUserRepository.existsByGameIdAndUserIdAndEnabled(gameId, sessionInfo.getCurrentUser().getId(), 1)) {
            return;
        }

        List<User> userList = new ArrayList<>();
        userList.add(sessionInfo.getCurrentUser());

        for (Integer userId : additionalPaxArray) {
            userList.add(userRepository.findByIdAndEnabled(userId, 1));
        }

        for (User user : userList) {
            UserPayment userPayment = new UserPayment();
            userPayment.setUserId(user.getId());
            userPayment.setGameId(gameId);
            userPayment.setFilepath("data/images/payment/" + gameId + "/" + user.getId() + ".jpg");
            userPayment.setInsBy(sessionInfo.getCurrentUser().getId());
            userPaymentRepository.save(userPayment);

            GameUser gameUser = new GameUser();
            gameUser.setGameId(gameId);
            gameUser.setUserId(user.getId());
            gameUser.setRegStatus(ERegistrationStatus.PAYMENT_VERIFICATION);
            gameUser.setInsBy(sessionInfo.getCurrentUser().getId());
            gameUserRepository.save(gameUser);

            gameUserId = gameUser.getId();

            mailService.paymentVerification(user, gameSchedule, gameType);
            fileService.writeImage(paymentProof, "data/images/payment/", user.getId(), gameId, 400, false);

            logger.info("user payment - {}", user.getUsername());
        }

        for (int i = 0; i < firstnameArray.length; i++) {
            UserCompanion userCompanion = new UserCompanion();
            userCompanion.setGameUserId(gameUserId);
            userCompanion.setFirstname(firstnameArray[i]);
            userCompanion.setLastname(lastnameArray[i]);
            userCompanion.setInsBy(sessionInfo.getCurrentUser().getId());
            userCompanionRepository.save(userCompanion);
        }
    }

    @Override
    public List<User> getUsersForVerification() {
        return userRepository.findAllByStatusAndEnabled(EUserStatus.ACCOUNT_VERIFICATION, 1);
    }

    @Override
    public List<UserEdit> getUsersEdit() {
        return userEditRepository.findAllByEnabled(1);
    }

    private void expireUserSession(String username) {
        final List<Object> allPrincipals = sessionRegistry.getAllPrincipals();

        for (final Object principal : allPrincipals) {
            final org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) principal;

            if (user.getUsername().equals(username)) {
                List<SessionInformation> activeUserSessions = sessionRegistry.getAllSessions(principal, false);

                for (SessionInformation sessionInformation : activeUserSessions) {
                    sessionInformation.expireNow();
                }
            }
        }
    }
}
