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
import org.cogz.web.enums.EUserEditStatus;
import org.cogz.web.enums.EUserStatus;
import org.cogz.web.model.User;
import org.cogz.web.model.UserEdit;
import org.cogz.web.model.UserNote;
import org.cogz.web.model.UserTask;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * @author altrax
 */
public interface IUserService {

    List<User> getUsers();

    User getUser(Integer userId);

    List<User> getUsersStrict();

    User getCurrentUser();

    void createUser(UserWithPasswordDto userDto);

    void createUser(MultipartFile validId, UserWithPasswordDto userDto) throws IOException;

    void editUser(UserDto userDto);

    void editUserFromProfile(UserDto userDto);

    void deactivateUser(Integer userId);

    void resetPassword(Integer userId, String password);

    void createUserEdit(MultipartFile validId, UserEditDto userEditDto) throws IOException;

    void changeUserEditStatus(Integer userId, EUserEditStatus status) throws IOException;

    UserTask acceptWaiver();

    void updateNotes(Integer userId, String notes);

    UserNote getUserNote(Integer userId);

    UserTask getWaiver();

    Boolean isWaiverAccepted(Integer userId);

    void changePicture(MultipartFile profilePic) throws IOException;

    void registerGame(MultipartFile paymentProof, Integer gameId, LocalDate gameSchedule, EGameType gameType,
            Integer[] additionalPaxArray, String[] firstnameArray, String[] lastnameArray) throws IOException;

    List<User> getUsersForVerification();

    List<UserEdit> getUsersEdit();

    void changeStatus(Integer userId, EUserStatus status) throws IOException;
}
