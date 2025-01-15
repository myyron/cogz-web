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
import org.cogz.web.dto.GameDto;
import org.cogz.web.enums.ERegistrationStatus;
import org.cogz.web.model.Game;
import org.cogz.web.model.GameUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.cogz.web.repository.GameRepository;
import org.cogz.web.repository.GameUserRepository;
import org.cogz.web.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author altrax
 */
@Service
public class GameServiceImpl implements IGameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameUserRepository gameUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Game> getGames() {
        return gameRepository.findAllByEnabled(1);
    }

    @Override
    @Transactional
    public Integer createGame(GameDto gameDto) {
        Game game = new ModelMapper().map(gameDto, Game.class);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        game.setInsBy(authentication.getName());

        return gameRepository.save(game).getId();
    }

    @Override
    @Transactional
    public void editGame(GameDto gameDto) {
        Game game = gameRepository.findById(gameDto.getId()).get();
        new ModelMapper().map(gameDto, game);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        game.setUpdBy(authentication.getName());
        game.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deactivateGame(Integer id) {
        Game game = gameRepository.findById(id).get();
        game.setEnabled(0);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        game.setUpdBy(authentication.getName());
        game.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void addUsers(Integer gameId, String usernames) {
        Game game = gameRepository.findById(gameId).get();
        for (String username : usernames.split(",")) {
            GameUser gameUser = new GameUser();
            gameUser.setGameId(game.getId());
            gameUser.setUserId(userRepository.findByUsername(username).get().getId());
            gameUser.setRegStatus(ERegistrationStatus.PENDING_PAYMENT);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            gameUser.setInsBy(authentication.getName());

            gameUserRepository.save(gameUser);
        }
    }

    @Override
    @Transactional
    public void removeUser(Integer id) {
        GameUser gameUser = gameUserRepository.findById(id).get();
        gameUser.setEnabled(0);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        gameUser.setUpdBy(authentication.getName());
        gameUser.setUpdDate(LocalDateTime.now());
    }

    @Override
    public List<GameUser> getPlayers(Integer gameId) {
        return gameUserRepository.findAllByGameIdAndEnabled(gameId, 1);
    }
}
