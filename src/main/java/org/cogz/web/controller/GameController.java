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
package org.cogz.web.controller;

import org.cogz.web.SessionInfo;
import org.cogz.web.dto.GameDto;
import org.cogz.web.dto.GameUserDto;
import org.cogz.web.dto.UserDto;
import org.cogz.web.enums.EGameStatus;
import org.cogz.web.enums.EGameType;
import org.cogz.web.enums.ERegistrationStatus;
import org.cogz.web.model.Game;
import org.cogz.web.model.GameUser;
import org.cogz.web.model.User;
import org.cogz.web.service.IFileService;
import org.cogz.web.service.IGameService;
import org.cogz.web.service.IUserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author altrax
 */
@RestController
@RequestMapping("/game")
public class GameController {

    Logger logger = LoggerFactory.getLogger(GameController.class);

    @Autowired
    private SessionInfo sessionInfo;

    @Autowired
    private IGameService gameService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IFileService fileService;

    @GetMapping("/list")
    public List<GameDto> getGames() {
        ModelMapper mapper = new ModelMapper();
        List<GameDto> result = new ArrayList<>();
        for (Game game : gameService.getGames()) {
            GameDto gameDto = mapper.map(game, GameDto.class);
            for (GameUser gameUser : gameService.getGameUsers(game.getId())) {
                GameUserDto gameUserDto = mapper.map(gameUser, GameUserDto.class);
                gameUserDto.setUser(mapper.map(userService.getUser(gameUser.getUserId()), UserDto.class));
                gameDto.getGameUserList().add(gameUserDto);
            }
            result.add(gameDto);
        }
        return result;
    }

    @GetMapping("/list-active")
    public List<GameDto> getActiveGames() {
        ModelMapper mapper = new ModelMapper();
        List<GameDto> result = new ArrayList<>();
        for (Game game : gameService.getActiveGames()) {
            GameDto gameDto = mapper.map(game, GameDto.class);
            for (GameUser gameUser : gameService.getGameUsers(game.getId())) {
                GameUserDto gameUserDto = mapper.map(gameUser, GameUserDto.class);
                gameUserDto.setUser(mapper.map(userService.getUser(gameUser.getUserId()), UserDto.class));
                gameDto.getGameUserList().add(gameUserDto);
            }
            result.add(gameDto);
        }
        return result;
    }

    @GetMapping("/list-payment")
    public List<GameDto> getGameUsersForVerification() {
        ModelMapper mapper = new ModelMapper();
        List<GameDto> result = new ArrayList<>();
        List<Game> gameList = gameService.getGames();
        for (Game game : gameList) {
            GameDto gameDto = mapper.map(game, GameDto.class);
            List<GameUser> gameUserList = gameService.getGameUsersForVerification(gameDto.getId());
            for (GameUser gameUser : gameUserList) {
                User user = userService.getUser(gameUser.getUserId());
                GameUserDto gameUserDto = mapper.map(gameUser, GameUserDto.class);
                gameUserDto.setUser(mapper.map(user, UserDto.class));
                gameDto.getGameUserList().add(gameUserDto);
            }
            result.add(gameDto);
        }
        return result;
    }

    @GetMapping("/list-user-candidate")
    public List<UserDto> getCandidateUsers(Integer gameId) {
        ModelMapper mapper = new ModelMapper();
        List<UserDto> result = new ArrayList<>();
        for (User user : userService.getUsers()) {
            if (!gameService.isUserRegistered(gameId, user.getId())) {
                result.add(mapper.map(user, UserDto.class));
            }
        }
        return result;
    }

    @GetMapping("/list-user-candidate-strict")
    public List<UserDto> getCandidateUsersStrict(Integer gameId) {
        ModelMapper mapper = new ModelMapper();
        List<UserDto> result = new ArrayList<>();
        for (User user : userService.getUsersStrict()) {
            if (user.getId().equals(sessionInfo.getCurrentUser().getId())) {
                continue;
            }
            if (!gameService.isUserRegistered(gameId, user.getId()) && userService.isWaiverAccepted(user.getId())) {
                result.add(mapper.map(user, UserDto.class));
            }
        }
        return result;
    }

    @GetMapping("/open-exist")
    public Boolean isOpenGameExisting() {
        return gameService.isOpenGameExisting();
    }

    @GetMapping("/generate-pdf")
    public void generateGameUserListPdf(Integer gameId, LocalDate schedule) throws Exception {
        ModelMapper mapper = new ModelMapper();
        List<GameUserDto> gameUserDtoList = new ArrayList<>();
        for (GameUser gameUser : gameService.getGameUsers(gameId)) {
            GameUserDto gameUserDto = mapper.map(gameUser, GameUserDto.class);
            User user = userService.getUser(gameUserDto.getUserId());
            gameUserDto.setUser(mapper.map(user, UserDto.class));
            gameUserDtoList.add(gameUserDto);
        }
        fileService.generateGameUserListPdf(schedule, gameUserDtoList);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGame(@RequestParam(required = false) MultipartFile banner, LocalDate schedule,
            Integer advanceDeadline, EGameType type, EGameStatus status) throws IOException {
        gameService.createGame(banner, schedule, advanceDeadline, type, status);
        return ResponseEntity.ok("Game created successfully.");
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editGame(@RequestParam(required = false) MultipartFile banner, Integer id, LocalDate schedule,
            Integer advanceDeadline, EGameType type, EGameStatus status) throws IOException {
        gameService.editGame(banner, id, schedule, advanceDeadline, type, status);
        return ResponseEntity.ok("Game edited successfully.");
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateGame(Integer id) {
        logger.info("deactivate game - {}", id);
        gameService.deactivateGame(id);
        return ResponseEntity.ok("Game deleted successfully.");
    }

    @PostMapping("/add-users")
    public ResponseEntity<?> addUsers(Integer gameId, Integer[] userIdArray) {
        gameService.addUsers(gameId, userIdArray);
        return ResponseEntity.ok("Users added to game successfully.");
    }

    @PostMapping("/remove-user")
    public ResponseEntity<?> removeUser(Integer id) {
        gameService.removeUser(id);
        return ResponseEntity.ok("User removed successfully.");
    }

    @PostMapping("/edit-user")
    public ResponseEntity<?> editUser(@RequestParam(required = false) MultipartFile paymentProof, Integer gameId, Integer gameUserId,
            Integer userId, ERegistrationStatus regStatus, Integer fps, Boolean absent, Boolean refunded) throws IOException {
        GameUserDto gameUserDto = new GameUserDto();
        gameUserDto.setGameId(gameId);
        gameUserDto.setId(gameUserId);
        gameUserDto.setUserId(userId);
        gameUserDto.setRegStatus(regStatus);
        gameUserDto.setFps(fps);
        gameUserDto.setAbsent(absent);
        gameUserDto.setRefunded(refunded);
        gameService.editUser(paymentProof, gameUserDto);
        return ResponseEntity.ok("User updated successfully.");
    }

    @PostMapping("/verification-paid")
    public ResponseEntity<?> setToPaid(Integer gameUserId) {
        gameService.setToPaid(gameUserId);
        return ResponseEntity.ok("Game user payment set to paid successfully.");
    }
}
