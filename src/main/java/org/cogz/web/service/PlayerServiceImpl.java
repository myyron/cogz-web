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
package org.cogz.web.service;

import java.util.ArrayList;
import java.util.List;
import org.cogz.web.dto.PlayerDto;
import org.cogz.web.entity.Player;
import org.cogz.web.repo.PlayerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The {@link PlayerService} implementation.
 *
 * @author Myyron Latorilla
 */
@Service
public class PlayerServiceImpl extends BaseService<PlayerRepository, Player> implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public List<PlayerDto> getAllPlayers() {
        List<PlayerDto> result = new ArrayList<>();
        for (Player player : playerRepository.findAllByEnabled(1)) {
            PlayerDto playerDto = new PlayerDto();
            BeanUtils.copyProperties(player, playerDto);
            result.add(playerDto);
        }
        return result;
    }

    @Override
    @Transactional
    public Long createPlayer(PlayerDto playerDto) {
        Player player = new Player();
        BeanUtils.copyProperties(playerDto, player);
        return add(player);
    }

    @Override
    @Transactional
    public Long editPlayer(PlayerDto playerDto) {
        Player player = playerRepository.findById(playerDto.getId()).orElse(null);
        BeanUtils.copyProperties(playerDto, player);
        return edit(player);
    }

    @Override
    @Transactional
    public Long deletePlayer(long id) {
        Player player = playerRepository.findById(id).orElse(null);
        player.setEnabled(0);
        return delete(player);
    }
}