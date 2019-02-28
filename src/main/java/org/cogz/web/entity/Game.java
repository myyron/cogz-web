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
package org.cogz.web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.cogz.web.enums.GameStatus;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

/**
 * The game entity class.
 *
 * @author Myyron Latorilla
 */
@Entity
@Table(name = "GAME")
public class Game extends BaseEntity {

    private String eventDesc;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Manila")
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date date;

    @Transient
    private int totalPlayers;

    @Transient
    private GameStatus gameStatus;

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "GAME_ID", nullable = false)
    @Where(clause = "enabled = 1")
    private List<GameExpense> gameExpenseList;

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "GAME_ID", nullable = false)
    @Where(clause = "enabled = 1")
    private List<GamePlayer> gamePlayerList;

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public GameStatus getGameStatus() {
        if (totalPlayers == 0) {
            gameStatus = GameStatus.NEW;
        } else {
            gameStatus = GameStatus.IN_PROGRESS;
        }
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<GameExpense> getGameExpenseList() {
        return gameExpenseList;
    }

    public void setGameExpenseList(List<GameExpense> gameExpenseList) {
        this.gameExpenseList = gameExpenseList;
    }

    public List<GamePlayer> getGamePlayerList() {
        return gamePlayerList;
    }

    public void setGamePlayerList(List<GamePlayer> gamePlayerList) {
        this.gamePlayerList = gamePlayerList;
    }
}
