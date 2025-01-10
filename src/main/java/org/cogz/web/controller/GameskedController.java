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
import org.cogz.web.dto.GameskedDto;
import org.cogz.web.service.IGameskedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author altrax
 */
@RestController
@RequestMapping("/account")
public class GameskedController {

    Logger logger = LoggerFactory.getLogger(GameskedController.class);

    @Autowired
    private IGameskedService gameskedService;

    @PostMapping("/add-gamesked")
    public ResponseEntity<?> addGamesked(@RequestBody GameskedDto gameskedDto) throws JsonProcessingException {
        logger.info("add gamesked - {}", new ObjectMapper().writeValueAsString(gameskedDto));
        gameskedService.addGamesked(gameskedDto);
        return ResponseEntity.ok("Game schedule added successfully.");
    }
}
