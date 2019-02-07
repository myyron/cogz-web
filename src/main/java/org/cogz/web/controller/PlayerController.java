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
import org.cogz.web.dto.PlayerDto;
import org.cogz.web.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

    @Autowired
    private PlayerService playerService;

    @RequestMapping(value = "/players/", method = RequestMethod.GET)
    public String showPlayersPage() {
        return "profiles/players";
    }

    @RequestMapping(value = "/players/list", method = RequestMethod.GET)
    @ResponseBody
    public String getPlayerList() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(playerService.getAllPlayers());
    }

    @RequestMapping(value = "/players/create", method = RequestMethod.POST)
    @ResponseBody
    public Long createPlayer(@RequestParam String playerDto) throws IOException {
        return playerService.createPlayer(new ObjectMapper().readValue(playerDto, PlayerDto.class));
    }

    @RequestMapping(value = "/players/delete", method = RequestMethod.POST)
    @ResponseBody
    public Long deletePlayer(@RequestParam long id) {
        return playerService.deletePlayer(id);
    }

    @RequestMapping(value = "/player/{id}", method = RequestMethod.GET)
    public String showPlayerDetailPage(@PathVariable("id") long id) {
        return "profiles/player";
    }

    @RequestMapping(value = "/player/edit", method = RequestMethod.POST)
    @ResponseBody
    public Long editPlayer(@RequestParam String playerDto) throws IOException {
        return playerService.editPlayer(new ObjectMapper().readValue(playerDto, PlayerDto.class));
    }
}
