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
package org.cogz.web.repo;

import java.util.List;
import org.cogz.web.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * The game repository interface.
 *
 * @author Myyron Latorilla
 */
public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("select distinct g from Game g left join fetch g.gamePlayerList left join fetch g.gameExpenseList "
            + "where g.enabled = 1")
    List<Game> findAllByEnabled();

    @Query("select g from Game g left join fetch g.gamePlayerList left join fetch g.gameExpenseList "
            + "where g.id = ?1 and g.enabled = 1")
    Game findById(long id);

    @Query("select g from Game g left join fetch g.gamePlayerList left join fetch g.gameExpenseList "
            + "where g.id = (select max(g.id) from Game g where g.enabled = 1)")
    Game findByMaxId();
}
