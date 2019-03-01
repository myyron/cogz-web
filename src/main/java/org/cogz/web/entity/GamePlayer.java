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

import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.Where;

/**
 * The game player entity class.
 *
 * @author Myyron Latorilla
 */
@Entity
@Table(name = "GAME_PLAYER")
public class GamePlayer extends BaseEntity {

    @Column(nullable = false)
    private Long inBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timeOut;

    private Long outBy;

    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Player player;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "GAME_PLAYER_ID", nullable = false)
    @Where(clause = "enabled = 1")
    private Set<GameFps> gameFpsList;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "GAME_PLAYER_ID", nullable = false)
    @Where(clause = "enabled = 1")
    private Set<GameFee> gameFeeList;

    public Long getInBy() {
        return inBy;
    }

    public void setInBy(Long inBy) {
        this.inBy = inBy;
    }

    public Date getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(Date timeOut) {
        this.timeOut = timeOut;
    }

    public Long getOutBy() {
        return outBy;
    }

    public void setOutBy(Long outBy) {
        this.outBy = outBy;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Set<GameFps> getGameFpsList() {
        return gameFpsList;
    }

    public void setGameFpsList(Set<GameFps> gameFpsList) {
        this.gameFpsList = gameFpsList;
    }

    public Set<GameFee> getGameFeeList() {
        return gameFeeList;
    }

    public void setGameFeeList(Set<GameFee> gameFeeList) {
        this.gameFeeList = gameFeeList;
    }
}
