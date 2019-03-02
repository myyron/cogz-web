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
package org.cogz.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.cogz.web.entity.GameExpense;
import org.cogz.web.enums.GameStatus;

/**
 * The game DTO class.
 *
 * @author Myyron Latorilla
 */
public class GameDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Manila")
    private Date date;

    private Long id;
    private String eventDesc;
    private int totalPlayers;
    private GameStatus gameStatus;
    private List<RegisteredPlayerDto> playerList = new ArrayList<>();
    private List<FeeSummaryDto> feeSummaryList = new ArrayList<>();
    private Set<GameExpense> gameExpenseList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public List<RegisteredPlayerDto> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<RegisteredPlayerDto> playerList) {
        this.playerList = playerList;
    }

    public Set<GameExpense> getGameExpenseList() {
        return gameExpenseList;
    }

    public void setGameExpenseList(Set<GameExpense> gameExpenseList) {
        this.gameExpenseList = gameExpenseList;
    }

    public List<FeeSummaryDto> getFeeSummaryList() {
        return feeSummaryList;
    }

    public void setFeeSummaryList(List<FeeSummaryDto> feeSummaryList) {
        this.feeSummaryList = feeSummaryList;
    }
}
