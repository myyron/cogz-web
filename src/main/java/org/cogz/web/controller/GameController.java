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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.cogz.web.dto.GameDto;
import org.cogz.web.dto.GameUserDto;
import org.cogz.web.dto.UserDto;
import org.cogz.web.enums.EGameStatus;
import org.cogz.web.enums.EGameType;
import org.cogz.web.enums.ERegistrationStatus;
import org.cogz.web.model.Game;
import org.cogz.web.model.GameUser;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private IGameService gameService;

    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public List<GameDto> getGames() {
        ModelMapper mapper = new ModelMapper();
        List<GameDto> result = new ArrayList<>();
        for (Game game : gameService.getGames()) {
            GameDto gameDto = mapper.map(game, GameDto.class);
            for (GameUser gameUser : gameService.getUsers(game.getId())) {
                GameUserDto gameUserDto = mapper.map(gameUser, GameUserDto.class);
                gameUserDto.setUser(mapper.map(userService.getUser(gameUser.getUserId()), UserDto.class));
                gameDto.getGameUserList().add(gameUserDto);
            }
            result.add(gameDto);
        }
        logger.info("game list - {}", result.size());
        return result;
    }

    @GetMapping("/list-active")
    public List<GameDto> getActiveGames() {
        ModelMapper mapper = new ModelMapper();
        List<GameDto> result = new ArrayList<>();
        for (Game game : gameService.getActiveGames()) {
            GameDto gameDto = mapper.map(game, GameDto.class);
            for (GameUser gameUser : gameService.getUsers(game.getId())) {
                GameUserDto gameUserDto = mapper.map(gameUser, GameUserDto.class);
                gameUserDto.setUser(mapper.map(userService.getUser(gameUser.getUserId()), UserDto.class));
                gameDto.getGameUserList().add(gameUserDto);
            }
            result.add(gameDto);
        }
        logger.info("active game list - {}", result.size());
        return result;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGame(@RequestParam(required = false) MultipartFile banner, LocalDate schedule, Integer advanceDeadline, EGameType type, EGameStatus status) throws IOException {
        gameService.createGame(banner, schedule, advanceDeadline, type, status);
        return ResponseEntity.ok("Game created successfully.");
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editGame(@RequestParam(required = false) MultipartFile banner, Integer id, LocalDate schedule, Integer advanceDeadline, EGameType type, EGameStatus status) throws IOException {
        gameService.editGame(banner, id, schedule, advanceDeadline, type, status);
        return ResponseEntity.ok("Game edited successfully.");
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateGame(@RequestParam Integer id) {
        logger.info("delete game - {}", id);
        gameService.deactivateGame(id);
        return ResponseEntity.ok("Game deleted successfully.");
    }

    @PostMapping("/add-users")
    public ResponseEntity<?> addUsers(@RequestParam Integer gameId, @RequestParam String usernames) {
        logger.info("add usernames {} to game {}", usernames, gameId);
        gameService.addUsers(gameId, usernames);
        return ResponseEntity.ok("User added to game successfully.");
    }

    @PostMapping("/remove-user")
    public ResponseEntity<?> removeUser(@RequestParam Integer id) {
        logger.info("remove user - {}", id);
        gameService.removeUser(id);
        return ResponseEntity.ok("User removed successfully.");
    }

    @PostMapping("/edit-user")
    public ResponseEntity<?> editUser(@RequestParam(required = false) MultipartFile paymentProof, Integer gameId, Integer gameUserId,
                                      ERegistrationStatus regStatus, Integer fps, Boolean absent, Boolean refunded) throws IOException {
        GameUserDto gameUserDto = new GameUserDto();
        gameUserDto.setGameId(gameId);
        gameUserDto.setId(gameUserId);
        gameUserDto.setRegStatus(regStatus);
        gameUserDto.setFps(fps);
        gameUserDto.setAbsent(absent);
        gameUserDto.setRefunded(refunded);
        gameService.editUser(paymentProof, gameUserDto);
        return ResponseEntity.ok("User updated successfully.");
    }
}
