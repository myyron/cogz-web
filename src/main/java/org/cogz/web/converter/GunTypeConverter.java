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
package org.cogz.web.converter;

import javax.persistence.AttributeConverter;
import org.cogz.web.enums.FeeType;
import org.cogz.web.enums.GunType;

/**
 * The {@link FeeType} attribute converter implementation.
 *
 * @author Myyron Latorilla
 */
public class GunTypeConverter implements AttributeConverter<GunType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(GunType attribute) {
        return attribute != null ? attribute.getCode() : null;
    }

    @Override
    public GunType convertToEntityAttribute(Integer dbData) {
        return GunType.getEnum(dbData);
    }

}
