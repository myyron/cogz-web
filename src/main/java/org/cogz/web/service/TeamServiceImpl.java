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

import org.cogz.web.model.Team;
import org.cogz.web.model.TeamUser;
import org.cogz.web.repository.TeamRepository;
import org.cogz.web.repository.TeamUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


/**
 * @author altrax
 */
@Service
public class TeamServiceImpl extends BaseService implements ITeamService {

    Logger logger = LoggerFactory.getLogger(TeamServiceImpl.class);

    @Autowired
    private TeamUserRepository teamUserRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private IFileService fileService;

    @Override
    public List<Team> getTeams() {
        return teamRepository.findAllByEnabled(1);
    }

    @Override
    public List<TeamUser> getTeamUsers(Integer teamId) {
        return teamUserRepository.findAllByTeamIdAndEnabled(teamId, 1);
    }

    @Override
    public Boolean isUserMember(Integer teamId, Integer userId) {
        return teamUserRepository.existsByTeamIdAndUserIdAndEnabled(teamId, userId, 1);
    }

    @Transactional
    @Override
    public void createTeam(MultipartFile logo, String name, Integer teamRepId) throws IOException {
        Team team = new Team();
        team.setName(name);
        team.setTeamRepId(teamRepId);
        team.setInsBy(sessionInfo.getCurrentUser().getId());
        teamRepository.save(team);

        fileService.writeImage(logo, "data/images/logo/", team.getId(), null, 400, true);
    }

    @Transactional
    @Override
    public void editTeam(MultipartFile logo, Integer id, String name, Integer teamRepId) throws IOException {
        Team team = teamRepository.findByIdAndEnabled(id, 1);
        team.setName(name);
        team.setTeamRepId(teamRepId);

        team.setUpdBy(sessionInfo.getCurrentUser().getId());
        team.setUpdDate(LocalDateTime.now());

        fileService.writeImage(logo, "data/images/logo/", team.getId(), null, 400, true);
    }

    @Transactional
    @Override
    public void deactivateTeam(Integer id) {
        Team team = teamRepository.findByIdAndEnabled(id, 1);
        team.setEnabled(0);

        team.setUpdBy(sessionInfo.getCurrentUser().getId());
        team.setUpdDate(LocalDateTime.now());
    }

    @Transactional
    @Override
    public void addUsers(Integer teamId, Integer[] userIdArray) {
        for (Integer userId : userIdArray) {
            TeamUser teamUser = new TeamUser();
            teamUser.setTeamId(teamId);
            teamUser.setUserId(userId);
            teamUser.setInsBy(sessionInfo.getCurrentUser().getId());

            teamUserRepository.save(teamUser);
        }
    }

    @Transactional
    @Override
    public void removeUser(Integer id) {
        TeamUser teamUser = teamUserRepository.findByIdAndEnabled(id, 1);
        teamUser.setEnabled(0);
        teamUser.setUpdBy(sessionInfo.getCurrentUser().getId());
        teamUser.setUpdDate(LocalDateTime.now());
    }
}
