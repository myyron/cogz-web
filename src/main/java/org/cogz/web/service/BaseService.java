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
package org.cogz.web.service;

import javax.persistence.Table;
import org.cogz.web.entity.BaseEntity;
import org.cogz.web.entity.EventLog;
import org.cogz.web.enums.EventOperType;
import org.cogz.web.repo.EventLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The base service.
 *
 * @author Myyron Latorilla
 * @param <T1> the repository type.
 * @param <T2> the entity type.
 */
public abstract class BaseService<T1 extends JpaRepository, T2 extends BaseEntity> {

    @Autowired
    private T1 repository;

    @Autowired
    private EventLogRepository eventLogRepository;

    @Autowired
    private SecurityService securityService;

    protected Long add(BaseEntity entity) {
        Long result = null;
        entity = (T2) repository.save(entity);
        result = entity.getId();
        EventLog eventLog = new EventLog();
        eventLog.setRefTable(entity.getClass().getAnnotation(Table.class).name());
        eventLog.setRefId(result);
        eventLog.setEventOper(EventOperType.ADD);
        eventLog.setEventUser(securityService.getAuthenticatedUser().getId());
        eventLogRepository.save(eventLog);
        return result;
    }

    protected Long edit(BaseEntity entity) {
        Long result = entity.getId();
        EventLog eventLog = new EventLog();
        eventLog.setRefTable(entity.getClass().getAnnotation(Table.class).name());
        eventLog.setRefId(result);
        eventLog.setEventOper(EventOperType.EDIT);
        eventLog.setEventUser(securityService.getAuthenticatedUser().getId());
        eventLogRepository.save(eventLog);
        return result;
    }

    protected Long delete(BaseEntity entity) {
        Long result = entity.getId();
        EventLog eventLog = new EventLog();
        eventLog.setRefTable(entity.getClass().getAnnotation(Table.class).name());
        eventLog.setRefId(result);
        eventLog.setEventOper(EventOperType.DELETE);
        eventLog.setEventUser(securityService.getAuthenticatedUser().getId());
        eventLogRepository.save(eventLog);
        return result;
    }
}
