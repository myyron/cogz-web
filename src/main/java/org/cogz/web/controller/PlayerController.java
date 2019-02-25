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
import org.cogz.web.entity.Gun;
import org.cogz.web.entity.Player;
import org.cogz.web.service.PlayerService;
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
 * The player controller.
 *
 * @author Myyron Latorilla
 */
@Controller
@RequestMapping("/profiles")
public class PlayerController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PlayerService playerService;

    @RequestMapping(value = "/players/", method = RequestMethod.GET)
    public String showPlayersPage() {
        return "profiles/players";
    }

    @RequestMapping(value = "/players/list", method = RequestMethod.GET)
    @ResponseBody
    public String getPlayerList() throws JsonProcessingException {
        String result = new ObjectMapper().writeValueAsString(playerService.getAllPlayers());
        logger.debug("player list: {}", result);
        return result;
    }

    @RequestMapping(value = "/players/create", method = RequestMethod.POST)
    @ResponseBody
    public Long createPlayer(@RequestParam String player) throws IOException {
        logger.debug("create player: {}", player);
        return playerService.createPlayer(new ObjectMapper().readValue(player, Player.class));
    }

    @RequestMapping(value = "/players/delete", method = RequestMethod.POST)
    @ResponseBody
    public Long deletePlayer(@RequestParam long id) {
        logger.debug("delete player: {}", id);
        return playerService.deletePlayer(id);
    }

    @RequestMapping(value = "/player/{id}", method = RequestMethod.GET)
    public String showPlayerDetailPage(Model model, @PathVariable("id") long id) throws JsonProcessingException {
        String player = new ObjectMapper().writeValueAsString(playerService.getPlayer(id));
        logger.debug("player detail: {}", player);
        model.addAttribute("player", player);
        return "profiles/player";
    }

    @RequestMapping(value = "/player/edit", method = RequestMethod.POST)
    @ResponseBody
    public String editPlayer(@RequestParam String player) throws IOException {
        logger.debug("edit player: {}", player);
        playerService.editPlayer(new ObjectMapper().readValue(player, Player.class));
        return player;
    }

    @RequestMapping(value = "/player/add-gun", method = RequestMethod.POST)
    @ResponseBody
    public String addGun(@RequestParam String gun) throws IOException {
        logger.debug("add gun: {}", gun);
        playerService.addGun(new ObjectMapper().readValue(gun, Gun.class));
        return gun;
    }

    @RequestMapping(value = "/player/edit-gun", method = RequestMethod.POST)
    @ResponseBody
    public String editGun(@RequestParam String gun) throws IOException {
        logger.debug("edit gun: {}", gun);
        playerService.editGun(new ObjectMapper().readValue(gun, Gun.class));
        return gun;
    }

    @RequestMapping(value = "/player/delete-gun", method = RequestMethod.POST)
    @ResponseBody
    public Long deleteGun(@RequestParam long playerId, @RequestParam long gunId) {
        logger.debug("delete gun: {},{}", playerId, gunId);
        return playerService.deleteGun(playerId, gunId);
    }
}
