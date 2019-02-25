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

import java.util.List;
import org.cogz.web.entity.Gun;
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
    public List<Player> getAllPlayers() {
        return playerRepository.findAllByEnabled();
    }

    @Override
    public Player getPlayer(long id) {
        return playerRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Long createPlayer(Player player) {
        return add(player);
    }

    @Override
    @Transactional
    public Long editPlayer(Player updatedPlayer) {
        Player player = playerRepository.findById(updatedPlayer.getId()).orElse(null);
        BeanUtils.copyProperties(updatedPlayer, player);
        return edit(player);
    }

    @Override
    @Transactional
    public Long deletePlayer(long id) {
        Player player = playerRepository.findById(id).orElse(null);
        player.setEnabled(0);
        return delete(player);
    }

    @Override
    @Transactional
    public Gun addGun(Gun gun) {
        Player player = playerRepository.findById(gun.getPlayerId()).orElse(null);
        player.getGuns().add(gun);
        edit(player);
        return gun;
    }

    @Override
    @Transactional
    public Long editGun(Gun gun) {
        Player player = playerRepository.findById(gun.getPlayerId()).orElse(null);
        for (Gun g : player.getGuns()) {
            if (g.getId().equals(gun.getId())) {
                BeanUtils.copyProperties(gun, g);
                break;
            }
        }
        return edit(player);
    }

    @Override
    @Transactional
    public Long deleteGun(long playerId, long gunId) {
        Player player = playerRepository.findById(playerId).orElse(null);
        for (Gun gun : player.getGuns()) {
            if (gun.getId() == gunId) {
                gun.setEnabled(0);
                break;
            }
        }
        return edit(player);
    }
}
