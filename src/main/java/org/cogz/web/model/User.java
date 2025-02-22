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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.cogz.web.enums.ERole;
import org.cogz.web.enums.EUserStatus;

import java.time.LocalDate;

/**
 *
 * @author altrax
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "USERS",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = {"username", "enabled"})
        })
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @NotBlank
    @Size(max = 120)
    private String firstname;

    @NotBlank
    @Size(max = 120)
    private String lastname;

    @Size(max = 40)
    private String callsign;

    @Size(max = 120)
    private String email;

    @Size(max = 20)
    private String mobileNumber;

    private LocalDate birthdate;
    private ERole role;
    private EUserStatus status;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
