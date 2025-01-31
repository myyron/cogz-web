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
            $("#editInputEmail").val(data.email);
            $("#editInputMobileNumber").val(data.mobileNumber);
            $("#editInputBirthdate").val(data.birthdate);

            $("#resetPasswordUserId").val(data.id);
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
                {date: '2025-01-26', count: 55}
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
        });

        $.ajax({
            url: '/game/open-exist',
            async: false
        }).done(function (data) {
            if (!data) {
                $('#taskList').append(`<a href="#" class="list-group-item list-group-item-action" data-bs-toggle="modal" data-bs-target="#createGameModal">
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
        });

        $.ajax({
            url: '/user/list-registration',
            async: false
        }).done(function (data) {
            $.each(data, function (i, data) {
                $('#taskList').append(`<a href="#" class="list-group-item list-group-item-action" data-bs-toggle="modal" data-bs-target="#verifyAccountRegistrationModal${i}">
                        <div class="row">
                            <div class="col-3 col-sm-2 col-md-1">
                                <div class=" ratio ratio-1x1 rounded-circle overflow-hidden">
                                    <img id="profilePic${i}" alt="" src="uploaded-images/profile/${data.id}.jpg">
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
                                <form id="verifyAccountRegistrationForm${i}">
                                    <div class="modal-header">
                                        <h1 class="modal-title h5">Verify New Account Registration</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>

                                    <div class="modal-body">
                                        <div class="row justify-content-center">
                                            <div class="col-auto">
                                                <ul class="list-unstyled">
                                                    <li>Username: <b>${data.username}</b></li>
                                                    <li>Fullname: <b>${data.lastname + ", " + data.firstname}</b></li>
                                                    <li>Email: <b>${data.email}</b></li>
                                                    <li>Mobile Number: <b>${data.mobileNumber}</b></li>                                                    
                                                    <li>Birthdate: <b>${data.birthdate}</b></li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                                        <button type="submit" class="btn btn-outline-danger">Banned</button>
                                        <button type="submit" class="btn btn-outline-primary" value="GOOD">Verified and Good</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>`);

                $("#verifyAccountRegistrationForm" + i).on("submit", function (e) {
                    let buttonVal = e.originalEvent.submitter.value;
                    let url = '/user/verification-good';
                    if (buttonVal !== 'GOOD') {
                        url = '/user/verification-banned';
                    }
                    $.ajax({
                        url: url,
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                        type: "post",
                        async: false,
                        dataType: "json",
                        data: "userId=" + data.id
                    }).always(function () {
                        $("#verifyAccountRegistrationModal" + i).modal("hide");
                    });
                });

                $('#profilePic' + i).on("error", function () {
                    $("#profilePic" + i).attr("src", "uploaded-images/profile/blank-profile.png");
                });
            });
        });

        $.ajax({
            url: '/user/list-modification',
            async: false
        }).done(function (data) {
            $.each(data, function (i, data) {
                $('#taskList').append(`<a href="#" class="list-group-item list-group-item-action" data-bs-toggle="modal" data-bs-target="#accountModificationRequestModal${i}">
                        <div class="row">
                            <div class="col-3 col-sm-2 col-md-1">
                                <div class=" ratio ratio-1x1 rounded-circle overflow-hidden">
                                    <img id="profilePic${i}" alt="" src="uploaded-images/profile/${data.id}.jpg">
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
                                <form id="accountModificationRequestForm${i}">
                                    <div class="modal-header">
                                        <h1 class="modal-title h5">Approve Account Modification</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>

                                    <div class="modal-body">
                                        <div class="row justify-content-center">
                                            <div class="col-auto">
                                                <ul class="list-unstyled">
                                                    <li>${data.username} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.username}</b></li>
                                                    <li>${data.lastname + ", " + data.firstname} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.lastname + ", " + data.userEdit.firstname}</b></li>
                                                    <li>${data.email} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.email}</b></li>
                                                    <li>${data.mobileNumber} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.mobileNumber}</b></li>                                                    
                                                    <li>${data.birthdate} <i class="bi bi-arrow-right"></i> <b>${data.userEdit.birthdate}</b></li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                                        <button type="submit" class="btn btn-outline-danger">Reject</button>
                                        <button type="submit" class="btn btn-outline-primary" value="APPROVE">Approve</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>`);

                $("#accountModificationRequestForm" + i).on("submit", function (e) {
                    let buttonVal = e.originalEvent.submitter.value;
                    let url = '/user/modification-approve';
                    if (buttonVal !== 'APPROVE') {
                        url = '/user/modification-reject';
                    }
                    $.ajax({
                        url: url,
                        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                        type: "post",
                        async: false,
                        dataType: "json",
                        data: "userId=" + data.id
                    }).always(function () {
                        $("#accountModificationRequestModal" + i).modal("hide");
                    });
                });

                $('#profilePic' + i).on("error", function () {
                    $("#profilePic" + i).attr("src", "uploaded-images/profile/blank-profile.png");
                });
            });
        });

        $.ajax({
            url: '/game/list-payment',
            async: false
        }).done(function (data) {
            $.each(data, function (i, data) {
                $.each(data.gameUserList, function (j, gameUser) {
                    $('#taskList').append(`<a href="#" class="list-group-item list-group-item-action" data-bs-toggle="modal" data-bs-target="#verifyPaymentModal${i + "-" + j}">
                            <div class="row">
                                <div class="col-3 col-sm-2 col-md-1">
                                    <div class=" ratio ratio-1x1 rounded-circle overflow-hidden">
                                        <img id="profilePic${i + "-" + j}" alt="" src="uploaded-images/profile/${gameUser.userId}.jpg">
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
                                            <div class="row">
                                                <div class="col-auto">
                                                    <ul class="list-unstyled">
                                                        <li>Game Schedule: <b>${data.schedule}</b></li>
                                                        <li>Game Type: <b>${data.type}</b></li>
                                                        <li>User: <b>${gameUser.user.lastname + ", " + gameUser.user.firstname}</b></li>
                                                    </ul>
                                                </div>
                                            </div>
                                            <div class="row justify-content-center">
                                                <div class="col">
                                                    <img id="paymentProof${i + "-" + j}" src="uploaded-images/payment/${data.id}/${gameUser.userId}.jpg" alt="" class="img-thumbnail">
                                                </div>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                                            <button type="submit" class="btn btn-outline-primary">Verified and Paid</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>`);

                    $("#verifyPaymentForm" + i + "-" + j).on("submit", function () {
                        $.ajax({
                            url: '/game/verification-paid',
                            contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                            type: "post",
                            async: false,
                            dataType: "json",
                            data: "gameUserId=" + gameUser.id
                        }).always(function () {
                            $("#verifyPaymentModal" + i + "-" + j).modal("hide");
                        });
                    });

                    $('#paymentProof' + i + "-" + j).on("error", function () {
                        $("#paymentProof" + i + "-" + j).attr("src", "uploaded-images/payment/blank-proof.png");
                    });

                    $('#profilePic' + i + "-" + j).on("error", function () {
                        $("#profilePic" + i + "-" + j).attr("src", "uploaded-images/profile/blank-profile.png");
                    });
                });
            });
        });

        if ($('#taskList').children().length === 0) {
            $('#taskList').append(`<div class="text-center">Nothing to show</div>`);
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
                url: "/user/reset-password",
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                type: "post",
                dataType: "json",
                data: "userId=" + $("#resetPasswordUserId").val() + "&password=" + $("#resetInputPassword").val()
            }).always(function () {
                $("#resetPasswordModal").modal("hide");
            });
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

        $('#banner').on("error", function () {
            $("#banner").attr("src", "uploaded-images/banner/default-banner.png");
        });

        $("#showCreateGameModal").on("click", function () {
            $("#createGameForm")[0].reset();
        });

        $("#showResetPasswordModal").on("click", function () {
            $("#resetPasswordForm")[0].reset();
        });

        $("#inputProfilePic").on("change", function () {

            let fd = new FormData();
            fd.append('profilePic', $('#inputProfilePic')[0].files[0]);

            $.ajax({
                url: "/user/change-pic",
                contentType: false,
                processData: false,
                type: "post",
                async: false,
                data: fd
            }).always(function () {
                $("#profilePic").attr("src", "uploaded-images/profile/" + userId + ".jpg");
            });
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
    }
}