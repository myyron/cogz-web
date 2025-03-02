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

class HomeAdmin {

    constructor() {

        let userId;
        let userStatus;

        $("#navHome").addClass("active");
        $("#navGameList").removeClass("active");
        $("#navUserList").removeClass("active");
        $("#navTeamList").removeClass("active");
        $("#navTools").removeClass("active");

        $.ajax({
            url: "/user/current"
        }).done(function (data) {

            userId = data.id;
            userStatus = data.status;

            localStorage.setItem('userId', userId);
            localStorage.setItem('role', data.role);

            $("#profilePic").attr("src", "uploaded-images/profile/" + userId + ".jpg");

            $("#username").text(data.username);
            $("#fullname").text(data.firstname + " " + data.lastname);
            $("#email").text(data.email);
            $("#mobileNumber").text(data.mobileNumber);

            setUserStatus();

            $("#editInputUsername").val(data.username);
            $("#editInputFirstname").val(data.firstname);
            $("#editInputLastname").val(data.lastname);
            $("#editInputCallsign").val(data.callsign);
            $("#editInputEmail").val(data.email);
            $("#editInputMobileNumber").val(data.mobileNumber);
            $("#editInputBirthdate").val(data.birthdate);

            $("#resetPasswordUserId").val(data.id);

            let callsign = data.callsign;
            if (callsign === null) {
                callsign = 'NO CALLSIGN';
            }

            let teamName = data.teamName;
            if (teamName !== null) {
                $("#teamLogo").attr("src", "uploaded-images/logo/" + data.teamId + ".jpg");
                $("#teamName").text(teamName);
                $("#teamCardCallsign").text(callsign);
                $("#teamCard").attr('hidden', false);
            }
        });

        $.ajax({
            url: "/game/list"
        }).done(function (data) {

            const chartData = [
                {date: '2024-12-01', count: 34},
                {date: '2024-12-08', count: 15},
                {date: '2024-12-15', count: 32},
                {date: '2024-12-22', count: 33},
                {date: '2024-12-29', count: 35},
                {date: '2025-01-05', count: 44},
                {date: '2025-01-12', count: 64},
                {date: '2025-01-19', count: 52},
                {date: '2025-01-26', count: 55},
                {date: '2025-02-02', count: 31},
                {date: '2025-02-09', count: 78}
            ];

            let earliestData;

            let isActiveFound = false;
            for (let i = 0; i < data.length; i++) {

                if (!isActiveFound) {
                    if (data[i].status !== 'CLOSED' && data[i].status !== 'ARCHIVED') {
                        isActiveFound = true;
                        earliestData = data[i];
                    }
                }

                chartData.push({date: data[i].schedule, count: data[i].gameUserList.length});
            }

            if (earliestData === undefined) {
                $("#gameDate").html(`<i class="bi bi-calendar-event-fill"></i> NONE`);
                $("#totalRegistered").html(`<span class="badge rounded-pill text-bg-info">0</span>`);
            } else {
                $("#gameDate").html(`<i class="bi bi-calendar-event-fill"></i> ${earliestData.schedule}`);
                $("#totalRegistered").html(`<span class="badge rounded-pill text-bg-info">${earliestData.gameUserList.length}</span>`);

                new Chart(document.getElementById('attendanceChart'), {
                    type: 'line',
                    options: {
                        plugins: {
                            legend: {
                                display: false
                            },
                            tooltip: {
                                enabled: true
                            }
                        },
                        scales: {
                            x: {
                                display: false
                            },
                            y: {
                                display: false
                            }
                        }
                    },
                    data: {
                        labels: chartData.map(row => row.date),
                        datasets: [{
                                data: chartData.map(row => row.count)
                            }]
                    }
                });
            }
        });

        $.when(
                $.ajax({
                    url: '/game/open-exist'
                }).done(function (data) {
            if (!data) {
                $('#taskList').append(`<a id="createGameTask" href="#" class="list-group-item list-group-item-action" data-bs-toggle="modal" data-bs-target="#createGameModal">
                    <div class="row">
                        <div class="col-3 col-sm-2 col-md-1">
                            <div class=" ratio ratio-1x1 rounded-circle overflow-hidden">
                                <img alt="" src="uploaded-images/profile/system.jpg">
                            </div>
                        </div>
                        <div class="col-9 col-sm-10 col-md-11 mt-1">
                            <ul class="list-unstyled">
                                <li class="fw-bolder">System</li>
                                <li class="fst-italic text-disabled" style="font-size: .8rem;">Create Game Schedule</li>
                            </ul>
                        </div>
                    </div></a>`);
            }
        }),
                $.ajax({
                    url: '/user/list-registration'
                }).done(function (data) {
            $.each(data, function (i, data) {
                $('#taskList').append(`<a id="verifyAccountRegistrationTask${i}" href="#" class="list-group-item list-group-item-action" data-bs-toggle="modal" data-bs-target="#verifyAccountRegistrationModal${i}">
                        <div class="row">
                            <div class="col-3 col-sm-2 col-md-1">
                                <div class=" ratio ratio-1x1 rounded-circle overflow-hidden">
                                    <img id="verifyAccountRegistrationProfilePic${i}" alt="" src="uploaded-images/profile/${data.id}.jpg">
                                </div>
                            </div>
                            <div class="col-9 col-sm-10 col-md-11 mt-1">
                                <ul class="list-unstyled">
                                    <li class="fw-bolder">${data.lastname + ", " + data.firstname}</li>
                                    <li class="fst-italic text-disabled" style="font-size: .8rem;">New Account Registration</li>
                                </ul>
                            </div>
                        </div>
                    </a>
                    <!-- verifyAccountRegistrationModal -->
                    <div class="modal fade" id="verifyAccountRegistrationModal${i}" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content rounded-4 shadow">
                                <form>
                                    <div class="modal-header">
                                        <h1 class="modal-title h5">Verify New Account Registration</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>

                                    <div class="modal-body">                                                                           
                                        <div class="row mb-3 justify-content-center">
                                            <div class="col">
                                                <div class="card">
                                                    <div class="card-body"> 
                                                        <div class="row justify-content-center">
                                                            <div class="col-auto">
                                                                <ul class="list-unstyled">
                                                                    <li>Username: <b>${data.username}</b></li>
                                                                    <li>Fullname: <b>${data.lastname + ", " + data.firstname}</b></li>
                                                                    <li>Callsign: <b>${data.callsign}</b></li>
                                                                    <li>Email: <b>${data.email}</b></li>
                                                                    <li>Mobile Number: <b>${data.mobileNumber}</b></li>                                                    
                                                                    <li>Birthdate: <b>${data.birthdate}</b></li>
                                                                </ul>
                                                            </div>
                                                        </div> 
                                                    </div>
                                                </div>  
                                            </div>
                                        </div>  
                                        <div class="row justify-content-center">
                                            <div class="col-auto">
                                                <img id="validId${i}" src="uploaded-images/id/${data.id}.jpg" alt="" class="img-thumbnail">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                                        <button id="bannedButton${i}" type="button" class="btn btn-outline-danger">
                                            <span id="bannedSpinner${i}" class="spinner-border spinner-border-sm"></span>
                                            <span class="visually-hidden" role="status">Loading...</span>Banned
                                        </button>
                                        <button id="goodButton${i}" type="button" class="btn btn-outline-primary">
                                            <span id="goodSpinner${i}" class="spinner-border spinner-border-sm"></span>
                                            <span class="visually-hidden" role="status">Loading...</span>Good
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>`);

                $("#verifyAccountRegistrationModal" + i).on('show.bs.modal', function () {
                    $("#goodSpinner" + i).attr('hidden', true);
                    $("#bannedSpinner" + i).attr('hidden', true);
                });

                $("#goodButton" + i).on("click", function (e) {

                    if (localStorage.getItem('role') === 'ROLE_PSEUDO_STAFF') {
                        bootbox.alert({
                            message: '<div class="text-center text-danger">Unauthorized</div>',
                            size: 'small'

                        }).init(function () {
                            $('.btn').removeClass('btn-primary');
                            $('.btn').addClass('btn-outline-primary');
                        });
                        return e.preventDefault();
                    }

                    $("#goodButton" + i).prop('disabled', true);
                    $("#goodSpinner" + i).attr('hidden', false);

                    $.ajax({
                        url: '/user/verification-good',
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                        type: "post",
                        dataType: "json",
                        data: "userId=" + data.id
                    }).always(function () {
                        $("#verifyAccountRegistrationTask" + i).remove();
                        $("#verifyAccountRegistrationModal" + i).modal("hide");
                        $("#verifyAccountRegistrationModal" + i).remove();
                        setTaskList();
                    });
                });

                $("#bannedButton" + i).on("click", function (e) {

                    if (localStorage.getItem('role') === 'ROLE_PSEUDO_STAFF') {
                        bootbox.alert({
                            message: '<div class="text-center text-danger">Unauthorized</div>',
                            size: 'small'

                        }).init(function () {
                            $('.btn').removeClass('btn-primary');
                            $('.btn').addClass('btn-outline-primary');
                        });
                        return e.preventDefault();
                    }

                    $("#bannedButton" + i).prop('disabled', true);
                    $("#bannedSpinner" + i).attr('hidden', false);

                    $.ajax({
                        url: '/user/verification-banned',
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                        type: "post",
                        dataType: "json",
                        data: "userId=" + data.id
                    }).always(function () {
                        $("#verifyAccountRegistrationTask" + i).remove();
                        $("#verifyAccountRegistrationModal" + i).modal("hide");
                        $("#verifyAccountRegistrationModal" + i).remove();
                        setTaskList();
                    });
                });

                $('#verifyAccountRegistrationProfilePic' + i).on("error", function () {
                    $("#verifyAccountRegistrationProfilePic" + i).attr("src", "uploaded-images/profile/blank-profile.png");
                });

                $('#validId' + i).on("error", function () {
                    $("#validId" + i).attr("src", "uploaded-images/id/blank-id.png");
                });
            });
        }),
                $.ajax({
                    url: '/user/list-modification'
                }).done(function (data) {
            $.each(data, function (i, data) {
                $('#taskList').append(`<a id="accountModificationRequestTask${i}" href="#" class="list-group-item list-group-item-action" data-bs-toggle="modal" data-bs-target="#accountModificationRequestModal${i}">
                        <div class="row">
                            <div class="col-3 col-sm-2 col-md-1">
                                <div class=" ratio ratio-1x1 rounded-circle overflow-hidden">
                                    <img id="accountModificationRequestProfilePic${i}" alt="" src="uploaded-images/profile/${data.id}.jpg">
                                </div>
                            </div>
                            <div class="col-9 col-sm-10 col-md-11 mt-1">
                                <ul class="list-unstyled">
                                    <li class="fw-bolder">${data.lastname + ", " + data.firstname}</li>
                                    <li class="fst-italic text-disabled" style="font-size: .8rem;">Account Modification Request</li>
                                </ul>
                            </div>
                        </div>
                    </a>                    
                    <!-- accountModificationRequestModal -->
                    <div class="modal fade" id="accountModificationRequestModal${i}" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content rounded-4 shadow">
                                <form">
                                    <div class="modal-header">
                                        <h1 class="modal-title h5">Approve Account Modification</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>

                                    <div class="modal-body">                                                                          
                                        <div class="row mb-3 justify-content-center">
                                            <div class="col">
                                                <div class="card">
                                                    <div class="card-body"> 
                                                        <div class="row justify-content-center">
                                                            <div class="col-auto">
                                                                <ul class="list-unstyled">
                                                                    <li>${data.username} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.username}</b></li>
                                                                    <li>${data.lastname + ", " + data.firstname} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.lastname + ", " + data.userEdit.firstname}</b></li>
                                                                    <li>${data.callsign} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.callsign}</b></li>
                                                                    <li>${data.email} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.email}</b></li>
                                                                    <li>${data.mobileNumber} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.mobileNumber}</b></li>                                                    
                                                                    <li>${data.birthdate} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.birthdate}</b></li>
                                                                </ul>
                                                            </div>
                                                        </div> 
                                                    </div> 
                                                </div>
                                            </div>  
                                        </div> 
                                        <div id="idComparison${i}"> 
                                            <div class="row justify-content-center">
                                                <div class="col-auto">
                                                    <img id="newValidId${i}" src="uploaded-images/id-edit/${data.id}.jpg" alt="" class="img-thumbnail">
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                                        <button id="rejectButton${i}" type="button" class="btn btn-outline-danger">
                                            <span id="rejectSpinner${i}" class="spinner-border spinner-border-sm"></span>
                                            <span class="visually-hidden" role="status">Loading...</span>Reject
                                        </button>
                                        <button id="approveButton${i}" type="button" class="btn btn-outline-primary">
                                            <span id="approveSpinner${i}" class="spinner-border spinner-border-sm"></span>
                                            <span class="visually-hidden" role="status">Loading...</span>Approve
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>`);

                $("#accountModificationRequestModal" + i).on('show.bs.modal', function () {
                    $("#approveSpinner" + i).attr('hidden', true);
                    $("#rejectSpinner" + i).attr('hidden', true);
                });

                $("#approveButton" + i).on("click", function (e) {

                    if (localStorage.getItem('role') === 'ROLE_PSEUDO_STAFF') {
                        bootbox.alert({
                            message: '<div class="text-center text-danger">Unauthorized</div>',
                            size: 'small'

                        }).init(function () {
                            $('.btn').removeClass('btn-primary');
                            $('.btn').addClass('btn-outline-primary');
                        });
                        return e.preventDefault();
                    }

                    $("#approveButton" + i).prop('disabled', true);
                    $("#approveSpinner" + i).attr('hidden', false);

                    $.ajax({
                        url: '/user/modification-approve',
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                        type: "post",
                        dataType: "json",
                        data: "userId=" + data.id
                    }).always(function () {
                        $("#accountModificationRequestTask" + i).remove();
                        $("#accountModificationRequestModal" + i).modal("hide");
                        $("#accountModificationRequestModal" + i).remove();
                        setTaskList();
                    });
                });

                $("#rejectButton" + i).on("click", function (e) {

                    if (localStorage.getItem('role') === 'ROLE_PSEUDO_STAFF') {
                        bootbox.alert({
                            message: '<div class="text-center text-danger">Unauthorized</div>',
                            size: 'small'

                        }).init(function () {
                            $('.btn').removeClass('btn-primary');
                            $('.btn').addClass('btn-outline-primary');
                        });
                        return e.preventDefault();
                    }

                    $("#rejectButton" + i).prop('disabled', true);
                    $("#rejectSpinner" + i).attr('hidden', false);

                    $.ajax({
                        url: '/user/modification-reject',
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                        type: "post",
                        dataType: "json",
                        data: "userId=" + data.id
                    }).always(function () {
                        $("#accountModificationRequestTask" + i).remove();
                        $("#accountModificationRequestModal" + i).modal("hide");
                        $("#accountModificationRequestModal" + i).remove();
                        setTaskList();
                    });
                });

                $('#accountModificationRequestProfilePic' + i).on("error", function () {
                    $("#accountModificationRequestProfilePic" + i).attr("src", "uploaded-images/profile/blank-profile.png");
                });

                $('#newValidId' + i).on("error", function () {
                    $("#idComparison" + i).html("");
                });
            });
        }),
                $.ajax({
                    url: '/game/list-payment'
                }).done(function (data) {
            $.each(data, function (i, data) {
                $.each(data.gameUserList, function (j, gameUser) {
                    $('#taskList').append(`<a id="verifyPaymentTask${i + "-" + j}" href="#" class="list-group-item list-group-item-action" data-bs-toggle="modal" data-bs-target="#verifyPaymentModal${i + "-" + j}">
                            <div class="row">
                                <div class="col-3 col-sm-2 col-md-1">
                                    <div class=" ratio ratio-1x1 rounded-circle overflow-hidden">
                                        <img id="verifyPaymentProfilePic${i + "-" + j}" alt="" src="uploaded-images/profile/${gameUser.userId}.jpg">
                                    </div>
                                </div>
                                <div class="col-9 col-sm-10 col-md-11 mt-1">
                                    <ul class="list-unstyled">
                                        <li class="fw-bolder">${gameUser.user.lastname + ", " + gameUser.user.firstname}</li>
                                        <li class="fst-italic text-disabled" style="font-size: .8rem;">Payment Verification - Game: ${data.schedule}</li>
                                    </ul>
                                </div>
                            </div>
                        </a>
                        <!-- verifyPaymentModal -->
                        <div class="modal fade" id="verifyPaymentModal${i + "-" + j}" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1">
                            <div class="modal-dialog">
                                <div class="modal-content rounded-4 shadow">
                                    <form id="verifyPaymentForm${i + "-" + j}">
                                        <div class="modal-header">
                                            <h1 class="modal-title h5">Verify Payment</h1>
                                            <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                        </div>

                                        <div class="modal-body">                                                                                                                      
                                            <div class="row mb-3 justify-content-center">
                                                <div class="col">
                                                    <div class="card">
                                                        <div class="card-body"> 
                                                            <div class="row">
                                                                <div class="col-auto">
                                                                    <ul class="list-unstyled">
                                                                        <li>Game Schedule: <b>${data.schedule}</b></li>
                                                                        <li>Game Type: <b>${data.type}</b></li>
                                                                        <li>User: <b>${gameUser.user.lastname + ", " + gameUser.user.firstname}</b></li>
                                                                    </ul>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row justify-content-center">
                                                <div class="col-auto">
                                                    <img id="paymentProof${i + "-" + j}" src="uploaded-images/payment/${data.id}/${gameUser.userId}.jpg" alt="" class="img-thumbnail">
                                                </div>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                                            <button id="paidButton${i + "-" + j}" type="button" class="btn btn-outline-primary">
                                                <span id="paidSpinner${i + "-" + j}" class="spinner-border spinner-border-sm"></span>
                                                <span class="visually-hidden" role="status">Loading...</span>Verified and Paid
                                            </button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>`);

                    $("#verifyPaymentModal" + i + "-" + j).on('show.bs.modal', function () {
                        $("#paidSpinner" + i + "-" + j).attr('hidden', true);
                    });

                    $("#paidButton" + i + "-" + j).on("click", function (e) {

                        if (localStorage.getItem('role') === 'ROLE_PSEUDO_STAFF') {
                            bootbox.alert({
                                message: '<div class="text-center text-danger">Unauthorized</div>',
                                size: 'small'

                            }).init(function () {
                                $('.btn').removeClass('btn-primary');
                                $('.btn').addClass('btn-outline-primary');
                            });
                            return e.preventDefault();
                        }

                        $("#paidButton" + i + "-" + j).prop('disabled', true);
                        $("#paidSpinner" + i + "-" + j).attr('hidden', false);

                        $.ajax({
                            url: '/game/verification-paid',
                            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                            type: "post",
                            dataType: "json",
                            data: "gameUserId=" + gameUser.id
                        }).always(function () {
                            $("#verifyPaymentTask" + i + "-" + j).remove();
                            $("#verifyPaymentModal" + i + "-" + j).modal("hide");
                            $("#verifyPaymentModal" + i + "-" + j).remove();
                            setTaskList();
                        });
                    });

                    $('#paymentProof' + i + "-" + j).on("error", function () {
                        $("#paymentProof" + i + "-" + j).attr("src", "uploaded-images/payment/blank-proof.png");
                    });

                    $('#verifyPaymentProfilePic' + i + "-" + j).on("error", function () {
                        $("#verifyPaymentProfilePic" + i + "-" + j).attr("src", "uploaded-images/profile/blank-profile.png");
                    });
                });
            });
        })).done(function () {
            setTaskList();
        });

        $("#editProfileButton").on("click", function () {

            $("#editProfileButton").prop('disabled', true);
            $("#editProfileSpinner").attr('hidden', false);

            let fd = new FormData();
            fd.append('id', userId);
            fd.append('username', $('#editInputUsername').val());
            fd.append('firstname', $('#editInputFirstname').val());
            fd.append('lastname', $('#editInputLastname').val());
            fd.append('callsign', $('#editInputCallsign').val());
            fd.append('email', $('#editInputEmail').val());
            fd.append('mobileNumber', $('#editInputMobileNumber').val());
            fd.append('birthdate', $('#editInputBirthdate').val());

            $.ajax({
                url: "/user/profile-edit",
                contentType: false,
                processData: false,
                type: "post",
                data: fd
            }).always(function () {
                $("#teamCardCallsign").text($('#editInputCallsign').val());
                $("#username").text($('#editInputUsername').val());
                $("#fullname").text($('#editInputFirstname').val() + " " + $('#editInputLastname').val());
                $("#email").text($('#editInputEmail').val());
                $("#mobileNumber").text($('#editInputMobileNumber').val());
                $("#editProfileModal").modal("hide");
                $("#editProfileButton").prop('disabled', false);
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
                data: "userId=" + $("#resetPasswordUserId").val() + "&password=" + $("#resetInputPassword").val()
            }).always(function () {
                $("#resetPasswordModal").modal("hide");
            });
        });

        $("#createGameButton").on("click", function () {

            $("#createGameButton").prop('disabled', true);
            $("#createGameSpinner").attr('hidden', false);

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
                data: fd
            }).always(function () {
                $("#createGameTask").remove();
                $("#createGameModal").modal("hide");
                $("#createGameButton").prop('disabled', false);
                setTaskList();
            });
        });

        $('#banner').on("error", function () {
            $("#banner").attr("src", "uploaded-images/banner/default-banner.png");
        });

        $("#showCreateGameModal").on("click", function () {
            $("#createGameForm")[0].reset();
        });

        $("#showResetPasswordModal").on("click", function () {
            $("#resetPasswordForm")[0].reset();
        });

        $("#createGameModal").on('show.bs.modal', function () {
            $("#createGameSpinner").attr('hidden', true);
        });

        $("#editProfileModal").on('show.bs.modal', function () {
            $("#editProfileSpinner").attr('hidden', true);
        });

        $("#inputProfilePic").on("change", function () {

            let fd = new FormData();
            fd.append('profilePic', $('#inputProfilePic')[0].files[0]);

            $.ajax({
                url: "/user/change-pic",
                contentType: false,
                processData: false,
                type: "post",
                data: fd
            }).always(function () {
                $("#profilePic").attr("src", "uploaded-images/profile/" + userId + ".jpg");
            });
        });

        $("#teamLogo").on("error", function () {
            $("#teamLogo").attr("src", "uploaded-images/logo/blank-logo.png");
        });

        function setUserStatus() {
            if (userStatus === 'ACCOUNT_VERIFICATION') {
                $("#userStatus").addClass("text-bg-warning");
                $("#userStatus").html('<i class="bi bi-question-circle"></i> Account Verification');
            } else if (userStatus === 'GOOD') {
                $("#userStatus").addClass("text-bg-success");
                $("#userStatus").html('<i class="bi bi-check-circle"></i> Verified');
            } else {
                $("#userStatus").attr({
                    "data-bs-toggle": "tooltip",
                    "data-bs-placement": "bottom",
                    "data-bs-title": "Contact a CoGZ Admin to know your violation details."
                });
                const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
                const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

                $("#userStatus").addClass("text-bg-danger");
                $("#userStatus").html('<i class="bi bi-x-circle"></i> BANNED');
            }
        }

        function setTaskList() {
            if ($('#taskList').children().length === 0) {
                $('#taskList').append(`<div class="text-center">Nothing to show</div>`);
            }
        }

        const stompClient = new StompJs.Client({
            brokerURL: 'ws://localhost:8080/gs-guide-websocket'
        });

        stompClient.onConnect = (frame) => {
            setConnected(true);
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/admin-tasks', (greeting) => {
                showGreeting(JSON.parse(greeting.body).content);
            });
        };

        stompClient.onWebSocketError = (error) => {
            console.error('Error with websocket', error);
        };

        stompClient.onStompError = (frame) => {
            console.error('Broker reported error: ' + frame.headers['message']);
            console.error('Additional details: ' + frame.body);
        };

        function connect() {
            stompClient.activate();
        }

        function disconnect() {
            stompClient.deactivate();
            setConnected(false);
            console.log("Disconnected");
        }

        function sendName() {
            stompClient.publish({
                destination: "/app/hello",
                body: JSON.stringify({'name': $("#name").val()})
            });
        }

        function showGreeting(message) {
            $("#greetings").append("<tr><td>" + message + "</td></tr>");
        }

        $(function () {
            $("form").on('submit', (e) => e.preventDefault());
            $("#connect").click(() => connect());
            $("#disconnect").click(() => disconnect());
            $("#send").click(() => sendName());
        });
    }
}