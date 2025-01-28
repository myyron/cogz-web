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

import org.cogz.web.dto.GameUserDto;
import org.cogz.web.enums.EGameStatus;
import org.cogz.web.enums.EGameType;
import org.cogz.web.enums.ERegistrationStatus;
import org.cogz.web.model.Game;
import org.cogz.web.model.GameUser;
import org.cogz.web.repository.GameRepository;
import org.cogz.web.repository.GameUserRepository;
import org.cogz.web.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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
    public List<Game> getOpenGames() {
        return gameRepository.findAllByStatusInAndEnabled(List.of(EGameStatus.OPEN), 1);
    }

    @Override
    @Transactional
    public void createGame(MultipartFile banner, LocalDate schedule, Integer advanceDeadline, EGameType type, EGameStatus status) throws IOException {
        Game game = new Game();
        game.setSchedule(schedule);
        game.setAdvanceDeadline(advanceDeadline);
        game.setType(type);
        game.setStatus(status);
        game.setInsBy(sessionInfo.getCurrentUser().getId());
        gameRepository.save(game);

        fileService.writeImage(banner, "data/images/banner/", game.getId(), null, 800,false);
    }

    @Override
    @Transactional
    public void editGame(MultipartFile banner, Integer id, LocalDate schedule, Integer advanceDeadline, EGameType type, EGameStatus status) throws IOException {
        Game game = gameRepository.findByIdAndEnabled(id, 1);
        game.setSchedule(schedule);
        game.setAdvanceDeadline(advanceDeadline);
        game.setType(type);
        game.setStatus(status);

        game.setUpdBy(sessionInfo.getCurrentUser().getId());
        game.setUpdDate(LocalDateTime.now());

        fileService.writeImage(banner, "data/images/banner/", game.getId(), null, 800,false);
    }

    @Override
    @Transactional
    public void deactivateGame(Integer id) {
        Game game = gameRepository.findByIdAndEnabled(id, 1);
        game.setEnabled(0);

        game.setUpdBy(sessionInfo.getCurrentUser().getId());
        game.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void addUsers(Integer gameId, String userIds) {
        for (String userId : userIds.split(",")) {
            GameUser gameUser = new GameUser();
            gameUser.setGameId(gameId);
            gameUser.setUserId(Integer.valueOf(userId));
            gameUser.setRegStatus(ERegistrationStatus.PAYMENT_VERIFICATION);
            gameUser.setInsBy(sessionInfo.getCurrentUser().getId());

            gameUserRepository.save(gameUser);
        }
    }

    @Override
    @Transactional
    public void removeUser(Integer id) {
        GameUser gameUser = gameUserRepository.findByIdAndEnabled(id, 1);
        gameUser.setEnabled(0);
        gameUser.setUpdBy(sessionInfo.getCurrentUser().getId());
        gameUser.setUpdDate(LocalDateTime.now());
    }

    @Override
    public List<GameUser> getGameUsers(Integer gameId) {
        return gameUserRepository.findAllByGameIdAndEnabled(gameId, 1);
    }

    @Override
    public List<GameUser> getGameUsersForVerification(Integer gameId) {
        return gameUserRepository.findAllByGameIdAndRegStatusAndEnabled(gameId, ERegistrationStatus.PAYMENT_VERIFICATION, 1);
    }

    @Override
    @Transactional
    public void editUser(MultipartFile paymentProof, GameUserDto gameUserDto) throws IOException {

        if (paymentProof != null) {
            fileService.writeImage(paymentProof, "data/images/payment/", gameUserDto.getId(), gameUserDto.getGameId(), 400,false);
        }

        GameUser gameUser = gameUserRepository.findByIdAndEnabled(gameUserDto.getId(), 1);
        gameUser.setRegStatus(gameUserDto.getRegStatus());
        gameUser.setFps(gameUserDto.getFps());
        gameUser.setAbsent(gameUserDto.getAbsent());
        gameUser.setRefunded(gameUserDto.getRefunded());
        gameUser.setUpdBy(sessionInfo.getCurrentUser().getId());
        gameUser.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void setToLock(Integer gameId) {
        Game game = gameRepository.findByIdAndEnabled(gameId, 1);
        game.setStatus(EGameStatus.LOCKED);
        game.setUpdBy(0);
        game.setUpdDate(LocalDateTime.now());
    }

    @Override
    @Transactional
    public void setToPaid(Integer gameUserId) {
        GameUser gameUser = gameUserRepository.findByIdAndEnabled(gameUserId, 1);
        gameUser.setRegStatus(ERegistrationStatus.PAID);
        gameUser.setUpdBy(sessionInfo.getCurrentUser().getId());
        gameUser.setUpdDate(LocalDateTime.now());
    }

    @Override
    public Boolean isUserRegistered(Integer gameId, Integer userId) {
        return gameUserRepository.existsByGameIdAndUserIdAndEnabled(gameId, userId, 1);
    }

    @Override
    public Boolean isOpenGameExisting() {
        return gameRepository.existsByStatusAndEnabled(EGameStatus.OPEN, 1);
    }
}