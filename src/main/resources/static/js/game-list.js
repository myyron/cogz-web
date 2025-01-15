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

class GameList {

    constructor() {

        $("#navHome").removeClass("active");
        $("#navGameList").addClass("active");
        $("#navUserList").removeClass("active");

        DataTable.type('date', 'className', 'dt-left');

        const gameListTable = new DataTable('#gameListTable', {
            ajax: {
                url: '/game/list',
                dataSrc: ''
            },
            columns: [
                {data: 'schedule'},
                {data: 'type'},
                {data: null,
                    render: function (data) {
                        return data.gameUserList.length;
                    }
                },
                {data: null,
                    render: function (data) {
                        if (data.status === 'OPEN') {
                            return '<span class="badge rounded-pill text-bg-primary">OPEN</span>';
                        } else if (data.status === 'LOCKED') {
                            return '<span class="badge rounded-pill text-bg-warning">LOCKED</span>';
                        } else {
                            return data.status;
                        }
                    }
                }
            ],
            select: true
        });

        $.ajax({
            url: '/user/list',
            async: false
        }).done(function (data) {
            $.each(data, function (i, data) {
                $('#inputAddUsers').append($('<option>', {
                    value: data.username,
                    text: data.lastname + ", " + data.firstname
                }));
            });
        });

        const regUserListTable = new DataTable('#regUserListTable', {
            "columns": [
                {data: null,
                    render: function (data) {
                        return data.user.lastname + ", " + data.user.firstname;
                    }
                },
                {data: "regStatus"}
            ],
            select: true
        });

        gameListTable.on('click', 'tbody tr', (e) => {
            let classList = e.currentTarget.classList;
            if (classList.contains('selected')) {
                classList.remove('selected');
                $("#showEditGameModal").addClass("disabled");
                $("#deleteGameButton").addClass("disabled");
                $("#showAddUsersModal").addClass("disabled");
                $("#showEditUserModal").addClass("disabled");
                $("#removeUserButton").addClass("disabled");
                $('#editInputId').val('');
                regUserListTable.clear().draw();
            } else {
                gameListTable.rows('.selected').nodes().each((row) => row.classList.remove('selected'));
                classList.add('selected');
                $("#showEditGameModal").removeClass("disabled");
                $("#deleteGameButton").removeClass("disabled");
                $("#showAddUsersModal").removeClass("disabled");

                let data = gameListTable.row('.selected').data();
                $('#editInputId').val(data.id);
                regUserListTable.clear();
                regUserListTable.rows.add(data.gameUserList).draw();
            }
        });

        regUserListTable.on('click', 'tbody tr', (e) => {
            let classList = e.currentTarget.classList;
            if (classList.contains('selected')) {
                classList.remove('selected');
                $("#showEditUserModal").addClass("disabled");
                $("#removeUserButton").addClass("disabled");
                $('#editInputGameUserId').val('');
            } else {
                gameListTable.rows('.selected').nodes().each((row) => row.classList.remove('selected'));
                classList.add('selected');
                $("#showEditUserModal").removeClass("disabled");
                $("#removeUserButton").removeClass("disabled");

                let data = regUserListTable.row('.selected').data();
                $('#editInputGameUserId').val(data.id);
            }
        });

        $("#createGameForm").on("submit", function () {
            $.ajax({
                url: "/game/create",
                contentType: "application/json",
                type: "post",
                async: false,
                dataType: "json",
                data: createDtoFromForm(document.querySelectorAll('#createGameForm input'), [{key: 'type', inputId: 'inputGameType'},
                    {key: 'status', inputId: 'inputGameStatus'}
                ])
            }).always(function () {
                $("#createGameModal").modal("hide");
                gameListTable.ajax.reload();
                $("#createGameForm")[0].reset();
            });
        });

        $("#editGameForm").on("submit", function () {
            $.ajax({
                url: "/game/edit",
                contentType: "application/json",
                type: "post",
                async: false,
                dataType: "json",
                data: createDtoFromForm(document.querySelectorAll('#editGameForm input'), [{key: 'type', inputId: 'editInputGameType'},
                    {key: 'status', inputId: 'editInputGameStatus'}
                ])
            }).always(function () {
                $("#editGameModal").modal("hide");
                gameListTable.ajax.reload();
                $("#editGameForm")[0].reset();
            });
        });

        $("#addUsersForm").on("submit", function () {
            $.ajax({
                url: "/game/add-users",
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                type: "post",
                async: false,
                dataType: "json",
                data: "gameId=" + $('#editInputId').val() + "&usernames=" + $("#inputAddUsers").val()
            }).always(function () {
                $("#inputAddUsersModal").modal("hide");
                regUserListTable.ajax.reload();
                $("#addUsersForm")[0].reset();
            });
        });

        $("#deleteGameButton").on("click", function () {
            bootbox.confirm({
                title: 'Delete Game?',
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
                            url: "/game/deactivate",
                            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                            type: "post",
                            async: false,
                            dataType: "json",
                            data: "id=" + gameListTable.row('.selected').data().id
                        }).always(function () {
                            gameListTable.ajax.reload();
                            $("#deleteGameButton").addClass("disabled");
                        });
                    }
                }
            });
        });

        $("#removeUserButton").on("click", function () {
            bootbox.confirm({
                title: 'Remove User?',
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
                            url: "/game/remove-user",
                            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                            type: "post",
                            dataType: "json",
                            data: "id=" + regUserListTable.row('.selected').data().id
                        }).always(function () {
                            regUserListTable.row('.selected').remove().draw();
                            $("#removeUserButton").addClass("disabled");
                        });
                    }
                }
            });
        });

        $('#editGameModal').on('show.bs.modal', function () {
            let data = gameListTable.row('.selected').data();
            $('#editInputId').val(data.id);
            $('#editInputSchedule').val(data.schedule);
            $('#editInputGameType').val(data.type);
            $('#editInputGameStatus').val(data.status);
        });

        $("#showCreateGameModal").on("click", function () {
            $("#createGameForm")[0].reset();
        });

        $("#showAddUsersModal").on("click", function () {

            $("#addUsersForm")[0].reset();

            new TomSelect("#inputAddUsers", {
                onItemAdd: function () {
                    this.setTextboxValue('');
                    this.refreshOptions();
                },
                maxItems: null
            });
        });
    }
}