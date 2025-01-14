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

function getCurrentUser() {
    $.ajax({
        url: "/user/current"
    }).done(function (data) {
        localStorage.setItem('username', data.username);
        localStorage.setItem('role', data.role);
    });
    return;
}

/**
 * Converts the form's input data to dto.
 * @param {type} elements
 * @param {array of objects} fields - array of object with key and fieldId attribute
 * @returns {String} in json string
 */
function createDtoFromForm(elements, fields) {
    const data = {};
    for (let i = 0; i < elements.length; i++) {
        let el = elements[i];
        let attrname = el.getAttribute("name");

        if (!attrname)
            continue;
        data[attrname] = el.value;
    }

    for (let i = 0; i < fields.length; i++) {
        let text = $("#" + fields[i].inputId + " :selected").text();
        if (fields[i].key === 'role') {
            text = "ROLE_" + text;
        }
        data[fields[i].key] = text;
    }

    let result = JSON.stringify(data);
    return result;
}

