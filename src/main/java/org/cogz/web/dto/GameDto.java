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
import lombok.Getter;
import lombok.Setter;
import org.cogz.web.enums.EGameStatus;
import org.cogz.web.enums.EGameType;

/**
 *
 * @author altrax
 */
@Getter
@Setter
public class GameDto {

    private Integer id;
    private LocalDate schedule;
    private EGameType type;
    private EGameStatus status;

    private List<GameUserDto> gameUserList = new ArrayList<>();
}
