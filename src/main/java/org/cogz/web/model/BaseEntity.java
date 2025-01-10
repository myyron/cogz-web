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

import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 *
 * @author altrax
 */
@MappedSuperclass
public class BaseEntity {

    private Integer enabled = 1;

    @NotBlank
    @Size(max = 20)
    private String insBy;

    @Size(max = 20)
    private String updBy;

    private LocalDateTime insDate = LocalDateTime.now();

    private LocalDateTime updDate;

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getInsBy() {
        return insBy;
    }

    public void setInsBy(String insBy) {
        this.insBy = insBy;
    }

    public String getUpdBy() {
        return updBy;
    }

    public void setUpdBy(String updBy) {
        this.updBy = updBy;
    }

    public LocalDateTime getInsDate() {
        return insDate;
    }

    public void setInsDate(LocalDateTime insDate) {
        this.insDate = insDate;
    }

    public LocalDateTime getUpdDate() {
        return updDate;
    }

    public void setUpdDate(LocalDateTime updDate) {
        this.updDate = updDate;
    }
}
