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
        $("#navGameList").removeClass("active");
        $("#navUserList").addClass("active");
        $("#navTeamList").removeClass("active");
        $("#navTools").removeClass("active");

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
                {data: null,
                    render: function (data) {
                        return data.role.substring(data.role.indexOf('_') + 1);
                    }
                },
                {data: null,
                    render: function (data) {
                        if (data.status === 'BANNED') {
                            return '<span class="badge rounded-pill text-bg-danger">BANNED</span>';
                        } else if (data.status === 'ACCOUNT_VERIFICATION') {
                            return "VERIFICATION";
                        } else {
                            return data.status;
                        }
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

        $("#createUserForm").on("submit", function (e) {
            if ($("#inputPassword").val() !== $("#inputPassword2").val()) {
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
                url: "/user/create",
                contentType: "application/json",
                type: "post",
                async: false,
                dataType: "json",
                data: createDtoFromForm(document.querySelectorAll('#createUserForm input'), [{key: 'role', inputId: 'inputRoleType'},
                    {key: 'status', inputId: 'inputUserStatus'}
                ])
            }).always(function () {
                $("#createUserModal").modal("hide");
            });
        });

        $("#editUserForm").on("submit", function (event) {
            $.ajax({
                url: "/user/edit",
                contentType: "application/json",
                type: "post",
                async: false,
                dataType: "json",
                data: createDtoFromForm(document.querySelectorAll('#editUserForm input'), [{key: 'role', inputId: 'editInputRoleType'},
                    {key: 'status', inputId: 'editInputUserStatus'}
                ])
            }).always(function () {
                $("#editUserModal").modal("hide");
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
                url: "/user/reset-password",
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                type: "post",
                dataType: "json",
                data: "userId=" + userListTable.row('.selected').data().id + "&password=" + $("#resetInputPassword").val()
            }).always(function () {
                $("#resetPasswordModal").modal("hide");
            });
        });

        $("#deleteUserButton").on("click", function () {
            bootbox.confirm({
                title: 'Delete User?',
                message: '<div class="text-center text-warning">Are you sure?</div>',
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
                            data: "userId=" + userListTable.row('.selected').data().id
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

            if (parseInt(localStorage.getItem('userId'), 10) !== data.id) {
                if ((data.role === "ROLE_ADMIN" || data.role === "ROLE_STAFF") && localStorage.getItem('role') === 'ROLE_STAFF') {
                    bootbox.alert({
                        message: '<div class="text-center text-danger">Unauthorized</div>',
                        size: 'small'

                    }).init(function () {
                        $('.btn').removeClass('btn-primary');
                        $('.btn').addClass('btn-outline-primary');
                    });
                    return e.preventDefault();
                }
            } else {
                $("#editInputRoleType").append('<option value="STAFF">STAFF</option>');
                $("#editInputRoleType").val("STAFF");
            }

            $('#editInputId').val(data.id);
            $('#editInputUsername').val(data.username);
            $('#editInputFirstname').val(data.firstname);
            $('#editInputLastname').val(data.lastname);
            $('#editInputEmail').val(data.email);
            $('#editInputMobileNumber').val(data.mobileNumber);
            $('#editInputBirthdate').val(data.birthdate);
            $('#editInputRoleType').val(data.role.substring(data.role.indexOf('_') + 1));
            $('#editInputUserStatus').val(data.status);
        });

        $("#showResetPasswordModal").on("click", function () {
            $("#resetPasswordForm")[0].reset();
        });

        $("#showCreateUserModal").on("click", function () {
            $("#createUserForm")[0].reset();
        });
    }
}