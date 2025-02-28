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

import org.cogz.web.dto.TeamDto;
import org.cogz.web.dto.TeamUserDto;
import org.cogz.web.dto.UserDto;
import org.cogz.web.model.Team;
import org.cogz.web.model.TeamUser;
import org.cogz.web.model.User;
import org.cogz.web.service.ITeamService;
import org.cogz.web.service.IUserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author altrax
 */
@RestController
@RequestMapping("/team")
public class TeamController {

    Logger logger = LoggerFactory.getLogger(TeamController.class);

    @Autowired
    private ITeamService teamService;

    @Autowired
    private IUserService userService;

    @GetMapping("/list")
    public List<TeamDto> getTeams() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        List<TeamDto> result = new ArrayList<>();
        for (Team team : teamService.getTeams()) {
            UserDto userDto = mapper.map(userService.getUser(team.getTeamRepId()), UserDto.class);
            TeamDto teamDto = mapper.map(team, TeamDto.class);
            teamDto.setTeamRep(userDto);
            for (TeamUser teamUser : teamService.getTeamUsers(team.getId())) {
                TeamUserDto teamUserDto = mapper.map(teamUser, TeamUserDto.class);
                teamUserDto.setUser(mapper.map(userService.getUser(teamUser.getUserId()), UserDto.class));
                teamDto.getTeamUserList().add(teamUserDto);
            }
            result.add(teamDto);
        }
        return result;
    }

    @GetMapping("/list-user-candidate")
    public List<UserDto> getCandidateUsers(Integer teamId) {
        ModelMapper mapper = new ModelMapper();
        List<UserDto> result = new ArrayList<>();
        for (User user : userService.getUsers()) {
            if (!teamService.isUserMember(teamId, user.getId())) {
                result.add(mapper.map(user, UserDto.class));
            }
        }
        return result;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestParam(required = false) MultipartFile logo, String name, Integer teamRepId) throws IOException {
        teamService.createTeam(logo, name, teamRepId);
        return ResponseEntity.ok("Team created successfully.");
    }

    @PostMapping("/edit")
    public ResponseEntity<?> editTeam(@RequestParam(required = false) MultipartFile logo, Integer id, String name, Integer teamRepId) throws IOException {
        teamService.editTeam(logo, id, name, teamRepId);
        return ResponseEntity.ok("Team edited successfully.");
    }

    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateTeam(Integer id) {
        logger.info("deactivate team - {}", id);
        teamService.deactivateTeam(id);
        return ResponseEntity.ok("Team deactivated successfully.");
    }

    @PostMapping("/add-users")
    public ResponseEntity<?> addUsers(Integer teamId, Integer[] userIdArray) {
        teamService.addUsers(teamId, userIdArray);
        return ResponseEntity.ok("Users added to team successfully.");
    }

    @PostMapping("/remove-user")
    public ResponseEntity<?> removeUser(Integer id) {
        teamService.removeUser(id);
        return ResponseEntity.ok("User removed from team successfully.");
    }
}
