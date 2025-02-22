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

class TeamList {

    constructor() {

        $("#navHome").removeClass("active");
        $("#navGameList").removeClass("active");
        $("#navUserList").removeClass("active");
        $("#navTeamList").addClass("active");
        $("#navTools").removeClass("active");

        const teamRepSelect = new TomSelect("#inputTeamRep", {
            onItemAdd: function () {
                this.setTextboxValue('');
                this.refreshOptions();
            },
            maxItems: 1,
            valueField: 'id',
            labelField: 'title',
            searchField: 'title'
        });

        const editTeamRepSelect = new TomSelect("#editInputTeamRep", {
            maxItems: 1,
            valueField: 'id',
            labelField: 'title',
            searchField: 'title'
        });

        const addTeamMembersSelect = new TomSelect("#inputAddTeamMembers", {
            onItemAdd: function () {
                this.setTextboxValue('');
                this.refreshOptions();
            },
            maxItems: null,
            valueField: 'id',
            labelField: 'title',
            searchField: 'title'
        });

        $.ajax({
            url: '/user/list',
            dataType: "json"
        }).done(function (data) {
            $.each(data, function (i, data) {
                let fullname = data.lastname + ", " + data.firstname;
                teamRepSelect.addOption({id: data.id, title: fullname});
                editTeamRepSelect.addOption({id: data.id, title: fullname});
            });
        });

        const teamListTable = new DataTable('#teamListTable', {
            ajax: {
                url: '/team/list',
                dataSrc: ''
            },
            columns: [
                {data: 'name'},
                {data: null,
                    render: function (data) {
                        return data.teamRep.lastname + ", " + data.teamRep.firstname;
                    }
                },
                {data: null,
                    render: function (data) {
                        return data.teamUserList.length;
                    }
                }
            ],
            rowId: 'id',
            select: true
        });

        const teamMemberListTable = new DataTable('#teamMemberListTable', {
            "columns": [
                {data: null,
                    render: function (data) {
                        return data.user.lastname + ", " + data.user.firstname;
                    }
                },
                {data: "user.callsign"}
            ],
            select: true
        });

        teamListTable.on('click', 'tbody tr', (e) => {
            let classList = e.currentTarget.classList;
            if (classList.contains('selected')) {
                classList.remove('selected');
                $("#showEditTeamModal").addClass("disabled");
                $("#deleteTeamButton").addClass("disabled");
                $("#showAddTeamMembersModal").addClass("disabled");
                $("#removeTeamMemberButton").addClass("disabled");
                $('#editInputId').val('');
                teamMemberListTable.clear().draw();
            } else {
                teamListTable.rows('.selected').nodes().each((row) => row.classList.remove('selected'));
                classList.add('selected');
                $("#showEditTeamModal").removeClass("disabled");
                $("#deleteTeamButton").removeClass("disabled");
                $("#showAddTeamMembersModal").removeClass("disabled");

                let data = teamListTable.row(e.currentTarget).data();
                $('#editInputId').val(data.id);
                teamMemberListTable.clear();
                teamMemberListTable.rows.add(data.teamUserList).draw();
            }
        });

        teamMemberListTable.on('click', 'tbody tr', (e) => {
            let classList = e.currentTarget.classList;
            if (classList.contains('selected')) {
                classList.remove('selected');
                $("#removeTeamMemberButton").addClass("disabled");
            } else {
                classList.add('selected');
                $("#removeTeamMemberButton").removeClass("disabled");
            }
        });

        $("#createTeamButton").on("click", function () {

            $("#createTeamSpinner").attr('hidden', false);

            let fd = new FormData();
            fd.append('name', $('#inputTeamName').val());
            fd.append('teamRepId', $('#inputTeamRep').val());
            fd.append('logo', $('#inputLogo')[0].files[0]);

            $.ajax({
                url: "/team/create",
                contentType: false,
                processData: false,
                type: "post",
                data: fd
            }).always(function () {
                $("#createTeamModal").modal("hide");
                $("#createTeamForm")[0].reset();
                teamRepSelect.clear();
                teamListTable.ajax.reload();
            });
        });

        $("#editTeamButton").on("click", function () {

            $("#editTeamSpinner").attr('hidden', false);

            let fd = new FormData();
            fd.append('id', $('#editInputId').val());
            fd.append('name', $('#editInputTeamName').val());
            fd.append('teamRepId', $('#editInputTeamRep').val());
            fd.append('logo', $('#editInputLogo')[0].files[0]);

            $.ajax({
                url: "/team/edit",
                contentType: false,
                processData: false,
                type: "post",
                data: fd
            }).always(function () {
                $("#editTeamModal").modal("hide");
                $("#editTeamForm")[0].reset();
                editTeamRepSelect.clear();
                teamListTable.ajax.reload();
            });
        });

        $("#deleteTeamButton").on("click", function () {
            bootbox.confirm({
                title: 'Delete Team?',
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
                            url: "/team/deactivate",
                            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                            type: "post",
                            dataType: "json",
                            data: "id=" + teamListTable.row('.selected').data().id
                        }).always(function () {
                            teamListTable.ajax.reload();
                            teamMemberListTable.clear().draw();
                            $("#deleteTeamButton").addClass("disabled");
                            $("#showEditTeamModal").addClass("disabled");
                        });
                    }
                }
            });
        });

        $("#addTeamMembersButton").on("click", function () {

            $("#addTeamMembersSpinner").attr('hidden', false);

            $.ajax({
                url: "/team/add-users",
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                type: "post",
                dataType: "json",
                data: "teamId=" + $('#editInputId').val() + "&userIdArray=" + $("#inputAddTeamMembers").val()
            }).always(function () {
                $("#addTeamMembersModal").modal("hide");

                let idArray = addTeamMembersSelect.getValue();

                for (let i = 0; i < idArray.length; i++) {
                    let userInfo = addTeamMembersSelect.getOption(idArray[i]).textContent;
                    let userInfoTokens = userInfo.split('-');
                    let nameTokens = userInfoTokens[0].split(',');
                    let firstname = $.trim(nameTokens[1]);
                    let lastname = $.trim(nameTokens[0]);

                    let callsign = null;
                    if (userInfoTokens.length === 2) {
                        callsign = $.trim(userInfoTokens[1]);
                    }

                    teamMemberListTable.row.add(
                            {"user": {
                                    "id": idArray[i],
                                    "firstname": firstname,
                                    "lastname": lastname,
                                    "callsign": callsign
                                }
                            }).draw();
                }

                addTeamMembersSelect.clear();
                addTeamMembersSelect.clearOptions();
            });
        });

        $("#removeTeamMemberButton").on("click", function () {
            bootbox.confirm({
                title: 'Remove Member?',
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
                            url: "/team/remove-user",
                            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                            type: "post",
                            dataType: "json",
                            data: "id=" + teamMemberListTable.row('.selected').data().id
                        }).always(function () {
                            teamMemberListTable.row('.selected').remove().draw();
                            $("#removeTeamMemberButton").addClass("disabled");
                        });
                    }
                }
            });
        });

        $("#createTeamModal").on('show.bs.modal', function () {
            $("#createTeamSpinner").attr('hidden', true);
        });

        $('#editTeamModal').on('show.bs.modal', function () {
            let data = teamListTable.row('.selected').data();
            $('#editInputId').val(data.id);
            $('#editInputTeamName').val(data.name);
            $("#editLogo").attr("src", "uploaded-images/logo/" + data.id + ".jpg");
            editTeamRepSelect.addItem(data.teamRepId);

            $("#editTeamSpinner").attr('hidden', true);
        });

        $('#addTeamMembersModal').on('show.bs.modal', function () {
            $.ajax({
                url: '/team/list-user-candidate',
                dataType: "json",
                data: "teamId=" + teamListTable.row('.selected').data().id
            }).done(function (data) {
                $.each(data, function (i, data) {
                    let callsign = '';
                    if (data.callsign !== null) {
                        callsign = " - " + data.callsign;
                    }
                    let userInfo = data.lastname + ", " + data.firstname + callsign;
                    addTeamMembersSelect.addOption({id: data.id, title: userInfo});
                });
            });

            $("#addTeamMembersSpinner").attr('hidden', true);
        });

        $('#editLogo').on("error", function () {
            $("#editLogo").attr("src", "uploaded-images/logo/blank-logo.png");
        });
    }
}