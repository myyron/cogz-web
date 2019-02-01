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
package org.cogz.web.enums;

import java.util.Objects;

/**
 * The event operation type constants.
 *
 * @author Myyron Latorilla
 */
public enum EventOperType {

    ADD(0),
    EDIT(1),
    DELETE(2);

    private final Integer code;

    EventOperType(Integer code) {
        this.code = code;
    }

    public static EventOperType getEnum(Integer value) {
        for (EventOperType v : values()) {
            if (Objects.equals(v.getCode(), value)) {
                return v;
            }
        }
        throw new IllegalArgumentException();
    }

    public Integer getCode() {
        return code;
    }
}
