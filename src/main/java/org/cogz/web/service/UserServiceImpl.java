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
package org.cogz.web.service;

import java.util.ArrayList;
import java.util.List;
import org.cogz.web.dto.UserDto;
import org.cogz.web.entity.User;
import org.cogz.web.repo.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * The {@link UserService} implementation.
 *
 * @author Myyron Latorilla
 */
@Service
public class UserServiceImpl extends BaseService<UserRepository, User> implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAllUsers(boolean enabled) {
        List<UserDto> result = new ArrayList<>();
        for (User user : userRepository.findAllByEnabled(1)) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            result.add(userDto);
        }
        return result;
    }

    @Override
    public Long createUser(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setPassword(passwordEncoder.encode(userDto.getNewPw()));
        return insert(user);
    }
}