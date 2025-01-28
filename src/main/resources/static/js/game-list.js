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
        $("#navTools").removeClass("active");

        let addUsersSelect = new TomSelect("#inputAddUsers", {
            onItemAdd: function () {
                this.setTextboxValue('');
                this.refreshOptions();
            },
            maxItems: null,
            valueField: 'id',
            labelField: 'title',
            searchField: 'title'
        });

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

                let data = gameListTable.row(e.currentTarget).data();
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
                classList.add('selected');
                $("#showEditUserModal").removeClass("disabled");
                $("#removeUserButton").removeClass("disabled");

                let data = regUserListTable.row(e.currentTarget).data();
                $('#editInputGameUserId').val(data.id);
            }
        });

        $("#createGameForm").on("submit", function () {

            let fd = new FormData();
            fd.append('banner', $('#inputBanner')[0].files[0]);
            fd.append('schedule', $('#inputSchedule').val());
            fd.append('advanceDeadline', $('#inputAdvanceDeadline').val());
            fd.append('type', $('#inputGameType').val());
            fd.append('status', $('#inputGameStatus').val());

            $.ajax({
                url: "/game/create",
                contentType: false,
                processData: false,
                type: "post",
                async: false,
                data: fd
            }).always(function () {
                $("#createGameModal").modal("hide");
            });
        });

        $("#editGameForm").on("submit", function () {

            let fd = new FormData();
            fd.append('id', $('#editInputId').val());
            fd.append('banner', $('#editInputBanner')[0].files[0]);
            fd.append('schedule', $('#editInputSchedule').val());
            fd.append('advanceDeadline', $('#editInputAdvanceDeadline').val());
            fd.append('type', $('#editInputGameType').val());
            fd.append('status', $('#editInputGameStatus').val());

            $.ajax({
                url: "/game/edit",
                contentType: false,
                processData: false,
                type: "post",
                async: false,
                data: fd
            }).always(function () {
                $("#createGameModal").modal("hide");
            });
        });

        $("#addUsersForm").on("submit", function () {
            $.ajax({
                url: "/game/add-users",
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                type: "post",
                async: false,
                dataType: "json",
                data: "gameId=" + $('#editInputId').val() + "&userIds=" + $("#inputAddUsers").val()
            }).always(function () {
                $("#addUsersModal").modal("hide");
            });
        });

        $("#editUserForm").on("submit", function () {

            let fd = new FormData();
            fd.append('paymentProof', $('#editInputPaymentProof')[0].files[0]);
            fd.append('gameId', $('#editInputId').val());
            fd.append('gameUserId', $('#editInputGameUserId').val());

            fd.append('regStatus', $('#editInputRegStatus').val());
            fd.append('fps', $('#editInputFps').val());
            fd.append('absent', $("#editInputAbsent").prop("checked") ? true : false);
            fd.append('refunded', $("#editInputRefunded").prop("checked") ? true : false);

            $.ajax({
                url: "/game/edit-user",
                contentType: false,
                processData: false,
                type: "post",
                async: false,
                data: fd
            }).always(function () {
                $("#editUserModal").modal("hide");
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
                            regUserListTable.clear().draw();
                            $("#deleteGameButton").addClass("disabled");
                            $("#showEditGameModal").addClass("disabled");
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
                            $("#showEditUserModal").addClass("disabled");
                        });
                    }
                }
            });
        });

        $('#editGameModal').on('show.bs.modal', function () {
            let data = gameListTable.row('.selected').data();
            $('#editInputId').val(data.id);
            $('#editInputSchedule').val(data.schedule);
            $('#editInputAdvanceDeadline').val(data.advanceDeadline);
            $('#editInputGameType').val(data.type);
            $('#editInputGameStatus').val(data.status);
            $("#editBanner").attr("src", "uploaded-images/banner/" + data.id + ".jpg");
        });

        $('#addUsersModal').on('show.bs.modal', function () {
            $.ajax({
                url: '/game/list-user-candidate',
                async: false,
                dataType: "json",
                data: "gameId=" + gameListTable.row('.selected').data().id
            }).done(function (data) {
                $.each(data, function (i, data) {
                    let fullname = data.lastname + ", " + data.firstname;
                    addUsersSelect.addOption({id: data.id, title: fullname});
                });
            });
        });

        $('#editUserModal').on('show.bs.modal', function () {
            let gameId = $('#editInputId').val();
            let gameUserId = $('#editInputGameUserId').val();
            $("#paymentProof").attr("src", "uploaded-images/payment/" + gameId + "/" + gameUserId + ".jpg");

            let data = regUserListTable.row('.selected').data();
            $('#editInputRegStatus').val(data.regStatus);
            $('#editInputFps').val(data.fps);
            $('#editInputAbsent').prop('checked', data.absent);
            $('#editInputRefunded').prop('checked', data.refunded);
        });

        $('#paymentProof').on("error", function () {
            $("#paymentProof").attr("src", "uploaded-images/payment/blank-proof.png");
        });

        $('#banner').on("error", function () {
            $("#banner").attr("src", "uploaded-images/banner/default-banner.png");
        });

        $('#editBanner').on("error", function () {
            $("#editBanner").attr("src", "uploaded-images/banner/default-banner.png");
        });

        $("#showCreateGameModal").on("click", function () {
            $("#createGameForm")[0].reset();
        });

        $("#showAddUsersModal").on("click", function () {
            addUsersSelect.clear();
        });
    }
}