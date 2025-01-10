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

class UserList {

    constructor() {

        $("#navHome").removeClass("active");
        $("#navUserList").addClass("active");

        const userListTable = new DataTable('#userListTable', {
            ajax: {
                url: '/user/list',
                dataSrc: ''
            },
            columns: [
                {data: null,
                    render: function (data) {
                        return data.lastname + ", " + data.firstname.split(" ")[0];
                    }
                },
                {data: 'username'},
                {data: null,
                    render: function (data) {
                        return data.role.split("_")[1];
                    }
                }
            ],
            select: true
        });

        userListTable.on('click', 'tbody tr', (e) => {
            let classList = e.currentTarget.classList;
            if (classList.contains('selected')) {
                classList.remove('selected');
                $("#showEditUserModal").addClass("disabled");
                $("#deleteUserButton").addClass("disabled");
            } else {
                userListTable.rows('.selected').nodes().each((row) => row.classList.remove('selected'));
                classList.add('selected');
                $("#showEditUserModal").removeClass("disabled");
                $("#deleteUserButton").removeClass("disabled");
            }
        });

        $("#createUserForm").on("submit", function (event) {
            $.ajax({
                url: "/user/create",
                contentType: "application/json",
                type: "post",
                async: false,
                dataType: "json",
                data: createDtoFromForm(document.querySelectorAll('#createUserForm input'), 'inputRoleType')
            }).always(function () {
                $("#createUserModal").modal("hide");
                userListTable.ajax.reload();
                $("#createUserForm")[0].reset();
            });
        });

        $("#editUserForm").on("submit", function (event) {
            $.ajax({
                url: "/user/edit",
                contentType: "application/json",
                type: "post",
                async: false,
                dataType: "json",
                data: createDtoFromForm(document.querySelectorAll('#editUserForm input'), 'editInputRoleType')
            }).always(function () {
                $("#editUserModal").modal("hide");
                userListTable.ajax.reload();
                $("#editUserForm")[0].reset();
            });
        });
        
        $("#resetPasswordForm").on("submit", function (e) {
            if ($("#resetInputPassword").val() !== $("#resetInputPassword2").val()) {
                bootbox.alert({
                    message: '<div class="text-center text-warning">Password do not match</div>',
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
                data: "username=" + userListTable.row('.selected').data().username + "&password=" + $("#resetInputPassword").val()
            }).always(function () {
                $("#resetPasswordModal").modal("hide");
                $("#resetPasswordForm")[0].reset();
            });
        });

        $("#deleteUserButton").on("click", function () {
            bootbox.confirm({
                title: 'Delete User?',
                message: 'Are you sure?',
                buttons: {
                    cancel: {
                        label: 'Cancel',
                        className: 'btn-outline-secondary'
                    },
                    confirm: {
                        label: 'Confirm',
                        className: 'btn-outline-primary'
                    }
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            url: "/user/deactivate",
                            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                            type: "post",
                            async: false,
                            dataType: "json",
                            data: "username=" + userListTable.row('.selected').data().username
                        }).always(function () {
                            userListTable.ajax.reload();
                            $("#deleteUserButton").addClass("disabled");
                        });
                    }
                }
            });
        });

        $('#editUserModal').on('show.bs.modal', function (e) {
            let data = userListTable.row('.selected').data();

            if (data.role === "ROLE_ADMIN" && localStorage.getItem('role') === 'ROLE_STAFF') {
                bootbox.alert({
                    message: '<div class="text-center text-danger">Unauthorized</div>',
                    size: 'small'

                }).init(function () {
                    $('.btn').removeClass('btn-primary');
                    $('.btn').addClass('btn-outline-primary');
                });
                return e.preventDefault();
            }

            $('#editInputUsername').val(data.username);
            $('#editInputPassword').val("dummy");
            $('#editInputFirstname').val(data.firstname);
            $('#editInputLastname').val(data.lastname);
            $('#editInputEmail').val(data.email);
            $('#editInputMobileNumber').val(data.mobileNumber);
            $('#editInputBirthdate').val(data.birthdate);
            $('#editInputRoleType').val(data.role.split("_")[1]);
        });

        $("#showCreateUserModal").on("click", function () {
            $("#createUserForm")[0].reset();
        });

        $('#inputFirstname').on('input', function () {
            let value = $(this).val();
            $('#inputUsername').val(parseFirstname(value).toLowerCase());
        });

        $('#inputLastname').on('input', function () {
            let value = $(this).val();
            let lastname = value.replace(/ /g, '');
            let firstname = parseFirstname($('#inputFirstname').val());
            let username = firstname + lastname;
            $('#inputUsername').val(username.toLowerCase());
        });

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

        /**
         * Converts the form's input data to dto.
         * @param {type} elements
         * @returns {String} in json string
         */
        function createDtoFromForm(elements, inputRoleTypeId) {
            const data = {};
            for (let i = 0; i < elements.length; i++) {
                let el = elements[i];
                let attrname = el.getAttribute("name");

                if (!attrname)
                    continue;
                data[attrname] = el.value;
            }

            data["role"] = "ROLE_" + $("#" + inputRoleTypeId + " :selected").text();

            let result = JSON.stringify(data);
            return result;
        }
    }
}