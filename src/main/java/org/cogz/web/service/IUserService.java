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
import org.cogz.web.enums.EUserEditStatus;
import org.cogz.web.enums.EUserStatus;
import org.cogz.web.model.User;
import org.cogz.web.model.UserEdit;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author altrax
 */
public interface IUserService {

    List<User> getUsers();

    User getUser(Integer userId);

    User getCurrentUser();

    void createUser(UserWithPasswordDto userDto);

    void editUser(UserDto userDto);

    void deactivateUser(Integer userId);

    void resetPassword(Integer userId, String password);

    void createUserEdit(UserEditDto userEditDto);

    void changeUserEditStatus(Integer userId, EUserEditStatus status);

    void acceptWaiver();

    Boolean isWaiverAccepted();

    void changePicture(MultipartFile profilePic) throws IOException;

    void registerGame(MultipartFile paymentProof, Integer gameId) throws IOException;

    List<User> getUsersForVerification();

    List<UserEdit> getUsersEdit();

    void changeStatus(Integer userId, EUserStatus status);
}
