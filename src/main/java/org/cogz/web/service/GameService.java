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
import org.cogz.web.entity.Game;
import org.cogz.web.entity.GameExpense;

/**
 * The game service interface.
 *
 * @author Myyron Latorilla
 */
public interface GameService {

    List<Game> getAllGames();

    GameDto getGame(long id);

    GameDto getCurrentGame();

    Long createGame(Game game);

    GameDto editGame(Game game);

    Long deleteGame(long id);

    GameExpense addExpense(GameExpense gameExpense);
}
