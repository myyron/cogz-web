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
import org.cogz.web.entity.GameExpense;
import org.cogz.web.service.GameExpenseService;
import org.cogz.web.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @Autowired
    private GameExpenseService gameExpenseService;

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

    @RequestMapping(value = "/game/current", method = RequestMethod.GET)
    public String showCurrentGameDetailPage(Model model) throws JsonProcessingException {
        String game = new ObjectMapper().writeValueAsString(gameService.getCurrentGame());
        logger.debug("game detail: {}", game);
        model.addAttribute("game", game);
        return "registration/game";
    }

    @RequestMapping(value = "/game/{id}", method = RequestMethod.GET)
    public String showGameDetailPage(Model model, @PathVariable("id") long id) throws JsonProcessingException {
        String game = new ObjectMapper().writeValueAsString(gameService.getGame(id));
        logger.debug("game detail: {}", game);
        model.addAttribute("game", game);
        return "registration/game";
    }

    @RequestMapping(value = "/game/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editGame(@RequestParam String game) throws IOException {
        logger.debug("edit game: {}", game);
        gameService.editGame(new ObjectMapper().readValue(game, Game.class));
        return game;
    }

    @RequestMapping(value = "/game/add-expense", method = RequestMethod.POST)
    @ResponseBody
    public String addExpense(@RequestParam String gameExpense) throws IOException {
        logger.debug("add expense: {}", gameExpense);
        GameExpense persistedGameExpense = gameService.addExpense(new ObjectMapper().readValue(gameExpense, GameExpense.class));
        return new ObjectMapper().writeValueAsString(persistedGameExpense);
    }

    @RequestMapping(value = "/game/edit-expense", method = RequestMethod.POST)
    @ResponseBody
    public String editExpense(@RequestParam String gameExpense) throws IOException {
        logger.debug("edit expense: {}", gameExpense);
        gameExpenseService.editExpense(new ObjectMapper().readValue(gameExpense, GameExpense.class));
        return gameExpense;
    }

    @RequestMapping(value = "/game/delete-expense", method = RequestMethod.POST)
    @ResponseBody
    public Long deleteExpense(@RequestParam long id) {
        logger.debug("delete expense: {}", id);
        return gameExpenseService.deleteExpense(id);
    }

    @RequestMapping(value = "/report/{id}", method = RequestMethod.GET)
    public String showReport(@PathVariable("id") long id) {
        return "registration/report";
    }
}
