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
import org.cogz.web.enums.EGameStatus;
import org.cogz.web.enums.ERegistrationStatus;
import org.cogz.web.model.Game;
import org.cogz.web.model.GameUser;
import org.cogz.web.model.User;
import org.cogz.web.repository.GameRepository;
import org.cogz.web.repository.GameUserRepository;
import org.cogz.web.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author altrax
 */
@Service
public class GameServiceImpl extends BaseService implements IGameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private GameUserRepository gameUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IFileService fileService;

    @Override
    public List<Game> getGames() {
        return gameRepository.findAllByEnabled(1);
    }

    @Override
    public List<Game> getActiveGames() {
        return gameRepository.findAllByStatusInAndEnabled(List.of(EGameStatus.OPEN, EGameStatus.LOCKED), 1);
    }

    @Override
    @Transactional
    public Integer createGame(GameDto gameDto) {
        Game game = new ModelMapper().map(gameDto, Game.class);
        game.setInsBy(sessionInfo.getCurrentUser().getId());
        return gameRepository.save(game).getId();
    }

    @Override
    @Transactional
    public void editGame(GameDto gameDto) {
        Game game = gameRepository.findById(gameDto.getId()).get();
        new ModelMapper().map(gameDto, game);

        game.setUpdBy(sessionInfo.getCurrentUser().getId());
        game.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void deactivateGame(Integer id) {
        Game game = gameRepository.findById(id).get();
        game.setEnabled(0);

        game.setUpdBy(sessionInfo.getCurrentUser().getId());
        game.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void addUsers(Integer gameId, String usernames) {
        for (String username : usernames.split(",")) {
            GameUser gameUser = new GameUser();

            User user = userRepository.findByUsername(username).get();

            if (gameUserRepository.existsByGameIdAndUserIdAndEnabled(gameId, user.getId(), 1)) {
                continue;
            }

            gameUser.setGameId(gameId);
            gameUser.setUserId(user.getId());
            gameUser.setRegStatus(ERegistrationStatus.PAYMENT_VERIFICATION);
            gameUser.setInsBy(sessionInfo.getCurrentUser().getId());

            gameUserRepository.save(gameUser);
        }
    }

    @Override
    @Transactional
    public void removeUser(Integer id) {
        GameUser gameUser = gameUserRepository.findById(id).get();
        gameUser.setEnabled(0);
        gameUser.setUpdBy(sessionInfo.getCurrentUser().getId());
        gameUser.setUpdDate(LocalDateTime.now());
    }

    @Override
    public List<GameUser> getUsers(Integer gameId) {
        return gameUserRepository.findAllByGameIdAndEnabled(gameId, 1);
    }

    @Override
    @Transactional
    public void editUser(MultipartFile paymentProof, GameUserDto gameUserDto) throws IOException {

        if (paymentProof != null) {
            fileService.writeImage(paymentProof, "data/images/payment/", gameUserDto.getId(), gameUserDto.getGameId());
        }

        GameUser gameUser = gameUserRepository.findByIdAndEnabled(gameUserDto.getId(), 1).get();
        gameUser.setRegStatus(gameUserDto.getRegStatus());
        gameUser.setFps(gameUserDto.getFps());
        gameUser.setAbsent(gameUserDto.getAbsent());
        gameUser.setRefunded(gameUserDto.getRefunded());
        gameUser.setUpdBy(sessionInfo.getCurrentUser().getId());
        gameUser.setUpdDate(LocalDateTime.now());
    }
}