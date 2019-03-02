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

import org.cogz.web.entity.GameExpense;
import org.cogz.web.repo.GameExpenseRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The {@link GameExpenseService} implementation.
 *
 * @author Myyron Latorilla
 */
@Service
public class GameExpenseServiceImpl extends BaseService<GameExpenseRepository, GameExpense> implements GameExpenseService {

    @Autowired
    private GameExpenseRepository gameExpenseRepository;

    @Override
    public Long editExpense(GameExpense updatedGameExpense) {
        GameExpense gameExpense = gameExpenseRepository.findById(updatedGameExpense.getId()).orElse(null);
        BeanUtils.copyProperties(updatedGameExpense, gameExpense);
        return edit(gameExpense);
    }

    @Override
    public Long deleteExpense(long gameExpenseId) {
        GameExpense gameExpense = gameExpenseRepository.findById(gameExpenseId).orElse(null);
        gameExpense.setEnabled(0);
        return edit(gameExpense);
    }
}
