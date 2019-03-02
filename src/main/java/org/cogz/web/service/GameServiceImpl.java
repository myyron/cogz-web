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
import org.cogz.web.dto.GameDto;
import org.cogz.web.dto.RegisteredPlayerDto;
import org.cogz.web.entity.Game;
import org.cogz.web.entity.GameExpense;
import org.cogz.web.entity.GamePlayer;
import org.cogz.web.repo.GameRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The {@link GameService} implementation.
 *
 * @author Myyron Latorilla
 */
@Service
public class GameServiceImpl extends BaseService<GameRepository, Game> implements GameService {

    @Autowired
    private GameRepository gameRepository;

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAllByEnabled();
    }

    @Override
    public GameDto getGame(long id) {
        GameDto result = new GameDto();
        Game game = gameRepository.findById(id);
        BeanUtils.copyProperties(game, result);
        for (GamePlayer gamePlayer : game.getGamePlayerList()) {
            RegisteredPlayerDto registeredPlayerDto = new RegisteredPlayerDto();
            registeredPlayerDto.setCallSign(gamePlayer.getPlayer().getCallSign());
            if (gamePlayer.getTimeOut() != null) {
                registeredPlayerDto.setCheckOut(true);
            }
            result.getPlayerList().add(registeredPlayerDto);
        }
        return result;
    }

    @Override
    @Transactional
    public Long createGame(Game game) {
        return add(game);
    }

    @Override
    @Transactional
    public Long editGame(Game updatedGame) {
        Game game = gameRepository.findById(updatedGame.getId()).orElse(null);
        BeanUtils.copyProperties(updatedGame, game);
        return edit(game);
    }

    @Override
    @Transactional
    public Long deleteGame(long id) {
        Game game = gameRepository.findById(id);
        game.setEnabled(0);
        return delete(game);
    }

    @Override
    public Long addExpense(GameExpense gameExpense) {
        Game game = gameRepository.findById(gameExpense.getGameId()).orElse(null);
        game.getGameExpenseList().add(gameExpense);
        return add(game);
    }
}
