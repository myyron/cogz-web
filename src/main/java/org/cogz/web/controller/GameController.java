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
package org.cogz.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.cogz.web.entity.Game;
import org.cogz.web.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The game controller.
 *
 * @author Myyron Latorilla
 */
@Controller
@RequestMapping("/registration")
public class GameController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private GameService gameService;

    @RequestMapping(value = "/games/", method = RequestMethod.GET)
    public String showGamesPage() {
        return "registration/games";
    }

    @RequestMapping(value = "/games/list", method = RequestMethod.GET)
    @ResponseBody
    public String getGameList() throws JsonProcessingException {
        String result = new ObjectMapper().writeValueAsString(gameService.getAllGames());
        logger.debug("game list: {}", result);
        return result;
    }

    @RequestMapping(value = "/games/create", method = RequestMethod.POST)
    @ResponseBody
    public Long createGame(@RequestParam String game) throws IOException {
        logger.debug("create game: {}", game);
        return gameService.createGame(new ObjectMapper().readValue(game, Game.class));
    }

    @RequestMapping(value = "/games/delete", method = RequestMethod.POST)
    @ResponseBody
    public Long deleteGame(@RequestParam long id) {
        logger.debug("delete game: {}", id);
        return gameService.deleteGame(id);
    }

    @RequestMapping(value = "/game/{id}", method = RequestMethod.GET)
    public String showGameDetail(@PathVariable("id") long id) {
        return "registration/game";
    }

    @RequestMapping(value = "/report/{id}", method = RequestMethod.GET)
    public String showReport(@PathVariable("id") long id) {
        return "registration/report";
    }
}
