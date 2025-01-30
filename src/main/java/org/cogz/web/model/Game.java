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
package org.cogz.web.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.cogz.web.enums.EGameStatus;
import org.cogz.web.enums.EGameType;

import java.time.LocalDate;

/**
 *
 * @author altrax
 */
@Getter
@Setter
@Entity
@Table(name = "GAME",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "schedule")
        })
public class Game extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate schedule;
    private Integer advanceDeadline;

    private EGameType type;
    private EGameStatus status;
}
