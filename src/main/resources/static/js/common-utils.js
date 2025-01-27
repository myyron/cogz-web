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

    if (fields) {
        for (let i = 0; i < fields.length; i++) {
            let val = $("#" + fields[i].inputId + " :selected").val();
            if (fields[i].key === 'role') {
                val = "ROLE_" + val;
            }
            data[fields[i].key] = val;
        }
    }

    let result = JSON.stringify(data);
    return result;
}

/**
 * Parses the firstname into appended first characters.
 * @param {type} firstname
 * @returns {String}
 */
function parseFirstname(firstname) {
    let tokens = firstname.split(" ");
    let result = "";
    for (let i = 0; i < tokens.length; i++) {
        result += tokens[i].substring(0, 1);
    }
    return result;
}

$("#resetPasswordForm").on("submit", function (e) {
    if ($("#resetInputPassword").val() !== $("#resetInputPassword2").val()) {
        bootbox.alert({
            message: '<div class="text-center text-danger">Password do not match</div>',
            size: 'small'

        }).init(function () {
            $('.btn').removeClass('btn-primary');
            $('.btn').addClass('btn-outline-primary');
        });
        return e.preventDefault();
    }

    $.ajax({
        url: "/user/reset",
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        type: "post",
        dataType: "json",
        data: "username=" + $("#username").text() + "&password=" + $("#resetInputPassword").val()
    }).always(function () {
        $("#resetPasswordModal").modal("hide");
    });
});

$("#profilePic").on("click", function () {
    $("#inputProfilePic").click();
});

$('#editInputFirstname').on('input', function () {
    let value = $(this).val();
    $('#editInputUsername').val(parseFirstname(value).toLowerCase() + $('#editInputLastname').val().toLowerCase());
});

$('#editInputLastname').on('input', function () {
    let value = $(this).val();
    let lastname = value.replace(/ /g, '');
    let firstname = parseFirstname($('#editInputFirstname').val());
    let username = firstname + lastname;
    $('#editInputUsername').val(username.toLowerCase());
});

$('#profilePic').on("error", function () {
    $("#profilePic").attr("src", "uploaded-images/profile/blank-profile.png");
});