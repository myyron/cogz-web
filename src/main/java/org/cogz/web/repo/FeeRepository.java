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
package org.cogz.web.repo;

import java.util.List;
import org.cogz.web.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * The fee repository interface.
 *
 * @author Myyron Latorilla
 */
public interface FeeRepository extends JpaRepository<Fee, Long> {

    Fee findByItemNameAndEnabled(String itemName, Integer enabled);

    List<Fee> findAllByEnabled(Integer enabled);
}
