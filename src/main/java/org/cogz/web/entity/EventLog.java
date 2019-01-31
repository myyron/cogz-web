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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.cogz.web.converter.EventOperTypeConverter;
import org.cogz.web.enums.EventOperType;

/**
 * The event log entity class.
 *
 * @author Myyron Latorilla
 */
@Entity
public class EventLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String refTable;

    @Column(nullable = false)
    private Long refId;

    @Column(nullable = false)
    @Convert(converter = EventOperTypeConverter.class)
    private EventOperType eventOper;

    @Column(nullable = false)
    private Long eventUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRefTable() {
        return refTable;
    }

    public void setRefTable(String refTable) {
        this.refTable = refTable;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public EventOperType getEventOper() {
        return eventOper;
    }

    public void setEventOper(EventOperType eventOper) {
        this.eventOper = eventOper;
    }

    public Long getEventUser() {
        return eventUser;
    }

    public void setEventUser(Long eventUser) {
        this.eventUser = eventUser;
    }
}
