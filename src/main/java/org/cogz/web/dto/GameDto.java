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
package org.cogz.web.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.cogz.web.enums.EGameStatus;
import org.cogz.web.enums.EGameType;

/**
 *
 * @author altrax
 */
public class GameDto {

    private Integer id;
    private LocalDate schedule;
    private EGameType type;
    private EGameStatus status;
    
    private List<GameUserDto> gameUserList = new ArrayList<>();

    public List<GameUserDto> getGameUserList() {
        return gameUserList;
    }

    public void setGameUserList(List<GameUserDto> gameUserList) {
        this.gameUserList = gameUserList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getSchedule() {
        return schedule;
    }

    public void setSchedule(LocalDate schedule) {
        this.schedule = schedule;
    }

    public EGameType getType() {
        return type;
    }

    public void setType(EGameType type) {
        this.type = type;
    }

    public EGameStatus getStatus() {
        return status;
    }

    public void setStatus(EGameStatus status) {
        this.status = status;
    }
}
