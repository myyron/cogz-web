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

import org.cogz.web.dto.GameDto;
import org.cogz.web.dto.GameUserDto;
import org.cogz.web.model.Game;
import org.cogz.web.model.GameUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author altrax
 */
public interface IGameService {

    List<Game> getGames();

    List<Game> getActiveGames();

    Integer createGame(GameDto gameDto);

    void editGame(GameDto gameDto);

    void deactivateGame(Integer id);

    void addUsers(Integer gameId, String usernames);

    void removeUser(Integer id);

    List<GameUser> getUsers(Integer gameId);

    void editUser(MultipartFile paymentProof, GameUserDto gameUserDto) throws IOException;
}
