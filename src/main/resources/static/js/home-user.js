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

class HomeUser {

    constructor() {

        $("#navHome").addClass("active");
        $("#navGameList").addClass("disabled");
        $("#navUserList").addClass("disabled");

        $.ajax({
            url: "/user/current"
        }).done(function (data) {
            $("#username").text(data.username);
            $("#fullname").text(data.firstname + " " + data.lastname);
            $("#email").text(data.email);
            $("#mobileNumber").text(data.mobileNumber);

            $("#editInputUsername").val(data.username);
            $("#editInputFirstname").val(data.firstname);
            $("#editInputLastname").val(data.lastname);
            $("#editInputEmail").val(data.email);
            $("#editInputMobileNumber").val(data.mobileNumber);
            $("#editInputBirthdate").val(data.birthdate);
        });

        $("#editProfileForm").on("submit", function (event) {
            $.ajax({
                url: "/user/create-useredit",
                contentType: "application/json",
                type: "post",
                dataType: "json",
                data: createDtoFromForm(document.querySelectorAll('#editProfileForm input'))
            }).always(function () {
                $("#editProfileModal").modal("hide");
                userListTable.ajax.reload();
                $("#editProfileForm")[0].reset();
            });
        });

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
                $("#resetPasswordForm")[0].reset();
            });
        });

        $('#editInputFirstname').on('input', function () {
            let value = $(this).val();
            $('#editInputUsername').val(parseFirstname(value).toLowerCase());
        });

        $('#editInputLastname').on('input', function () {
            let value = $(this).val();
            let lastname = value.replace(/ /g, '');
            let firstname = parseFirstname($('#editInputFirstname').val());
            let username = firstname + lastname;
            $('#editInputUsername').val(username.toLowerCase());
        });
    }
}