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

        let userData;

        $("#navHome").addClass("active");
        $("#navTeamList").removeClass("active");
        $("#navTools").removeClass("active");

        stompConnect();

        let setUserData = function () {
            $.ajax({
                url: "/user/current"
            }).done(function (data) {

                userData = data;

                $("#profilePic").attr("src", "uploaded-images/profile/" + data.id + ".jpg");

                $("#username").text(data.username);
                $("#fullname").text(data.firstname + " " + data.lastname);
                $("#email").text(data.email);
                $("#mobileNumber").text(data.mobileNumber);

                changeToWaiverAccepted(data.waiverAccepted);
                setUserStatus();

                $("#editInputId").val(data.id);
                $("#editInputUsername").val(data.username);
                $("#editInputFirstname").val(data.firstname);
                $("#editInputLastname").val(data.lastname);
                $("#editInputCallsign").val(data.callsign);
                $("#editInputEmail").val(data.email);
                $("#editInputMobileNumber").val(data.mobileNumber);
                $("#editInputBirthdate").val(data.birthdate);

                $("#validId").attr("src", "uploaded-images/id/" + data.id + ".jpg");

                $("#resetPasswordUserId").val(data.id);

                let callsign = data.callsign;
                if (!callsign) {
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
        };

        setUserData();

        $("#editProfileButton").on("click", function (e) {

            if (!hasEditChanges()) {
                bootbox.alert({
                    message: '<div class="text-center text-danger">No changes found</div>',
                    size: 'small'

                }).init(function () {
                    $('.btn').removeClass('btn-primary');
                    $('.btn').addClass('btn-outline-primary');
                });
                return e.preventDefault();
            }

            $('#editProfileButton').prop('disabled', true);
            $("#editProfileSpinner").attr('hidden', false);

            let fd = new FormData();
            fd.append('username', $('#editInputUsername').val());
            fd.append('firstname', $('#editInputFirstname').val());
            fd.append('lastname', $('#editInputLastname').val());
            fd.append('callsign', $('#editInputCallsign').val());
            fd.append('email', $('#editInputEmail').val());
            fd.append('mobileNumber', $('#editInputMobileNumber').val());
            fd.append('birthdate', $('#editInputBirthdate').val());
            fd.append('validId', $('#editInputValidId')[0].files[0]);

            $.ajax({
                url: "/user/create-useredit",
                contentType: false,
                processData: false,
                type: "post",
                data: fd
            }).always(function () {
                $("#editProfileModal").modal("hide");
                $("#editInputValidId").val("");
                $("#validId").attr("src", "images/blank-id.png");
                $('#editProfileButton').prop('disabled', false);
                stompSendAction('ACCOUNT_MODIFICATION');
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
                data: fd
            }).always(function () {
                $("#profilePic").attr("src", "uploaded-images/profile/" + userData.id + ".jpg");
            });
        });

        $("#editProfileModal").on('show.bs.modal', function () {
            $("#editProfileSpinner").attr('hidden', true);
        });

        $('#waiverModal').on('show.bs.modal', function () {
            if (userData.waiverAccepted) {
                $('#waiverModalFooter').html(`<span class="text-disabled fst-italic" style="font-size: .7rem">You already agreed on the terms of the waiver<br>
                    <b>Agreed on:</b> ${new Date(userData.waiverAcceptedDate).toLocaleString()}</span>
                    <button type="button" class="btn btn-outline-primary" data-bs-dismiss="modal">Close</button>`);
            } else {
                $('#waiverModalFooter').html(`<button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Skip</button>
                    <button id="waiverAgreeButton" type="button" class="btn btn-outline-primary">
                        <span id="waiverAgreeSpinner" class="spinner-border spinner-border-sm"></span>
                        <span class="visually-hidden" role="status">Loading...</span>I Agree
                    </button>`);
                $("#waiverAgreeSpinner").attr('hidden', true);
            }

            $("#waiverAgreeButton").on("click", function () {

                $('#waiverAgreeButton').prop('disabled', true);
                $("#waiverAgreeSpinner").attr('hidden', false);

                $.ajax({
                    url: "/user/accept-waiver",
                    type: "post"
                }).done(function (data) {
                    userData.waiverAcceptedDate = new Date(data);
                }).always(function () {
                    userData.waiverAccepted = true;
                    changeToWaiverAccepted(true);
                    $("#waiverModal").modal("hide");
                    $('#waiverAgreeButton').prop('disabled', false);
                });
            });
        });

        $('#validId').on("error", function () {
            $("#validId").attr("src", "uploaded-images/id/blank-id.png");
        });

        $("#teamLogo").on("error", function () {
            $("#teamLogo").attr("src", "uploaded-images/logo/blank-logo.png");
        });

        function changeToWaiverAccepted(accepted) {
            if (accepted) {
                $("#taskWaiverStatus").removeClass("text-bg-warning");
                $("#taskWaiverStatus").addClass("text-bg-success");
                $("#taskWaiverStatus").text("Done");

                $("#profileWaiverStatus").removeClass("text-bg-warning");
                $("#profileWaiverStatus").addClass("text-bg-success");
                $("#profileWaiverStatus").html('<i class="bi bi-check-circle"></i> Waiver');
            } else {
                $("#taskWaiverStatus").removeClass("text-bg-success");
                $("#taskWaiverStatus").addClass("text-bg-warning");
                $("#taskWaiverStatus").text("Not done");

                $("#profileWaiverStatus").removeClass("text-bg-success");
                $("#profileWaiverStatus").addClass("text-bg-warning");
                $("#profileWaiverStatus").html('<i class="bi bi-question-circle"></i> Waiver');
            }
        }

        function setUserStatus() {
            if (userData.status === 'ACCOUNT_VERIFICATION') {
                $("#userStatus").addClass("text-bg-warning");
                $("#userStatus").html('<i class="bi bi-question-circle"></i> Verifying Account...');
                $("#gamelist").html(`<div class="text-center">Nothing to show</div>`);
            } else if (userData.status === 'GOOD') {
                $("#userStatus").removeClass("text-bg-warning");
                $("#userStatus").addClass("text-bg-success");
                $("#userStatus").html('<i class="bi bi-check-circle"></i> Verified');
                $.ajax({
                    url: "/game/list-active"
                }).done(function (data) {

                    if (data.length > 0) {
                        $("#gamelist").html('');
                    }

                    for (let i = 0; i < data.length; i++) {

                        let badgeColor = '<span class="badge text-bg-primary">';
                        if (data[i].status !== "OPEN") {
                            badgeColor = '<span class="badge text-bg-warning">';
                        }

                        $("#gamelist").append(`<li class="list-group-item">
                    <div class="row mb-2 justify-content-center">
                        <div class="col-auto">
                            <img id="gameBanner${i}" alt="" class="img-thumbnail"
                                 src="uploaded-images/banner/${data[i].id}.jpg">
                        </div>
                    </div>
                    <div class="row mb-2 justify-content-center">
                        <div class="col">
                            <div id="timeLeft${i}" class="flipdown"></div>
                        </div>
                        <div class="col">
                            <div class="text-end">
                                <ul class="list-unstyled">
                                    <li class="fw-bolder"><i class="bi bi-calendar-event-fill"></i> ${data[i].schedule}
                                    </li>
                                    <li class="mb-1" style="font-size: .8rem;">${data[i].type} Game</li>
                                    <li id="gamestatus${i}">${badgeColor}
                                        <i class="bi bi-activity"></i> ${data[i].status}</span>
                                    </li>
                                    <li id="regStatus${i}" class="mt-2">
                                        <button id="registerGameButton${i}" type="button" class="btn btn-outline-primary"
                                                data-bs-toggle="modal"
                                                data-bs-target="#registerGameModal${i}">Register
                                        </button>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <!-- registerGameModal -->
                    <div class="modal fade" id="registerGameModal${i}" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content rounded-4 shadow">
                                <form id="registerGameForm${i}">

                                    <div class="modal-header">
                                        <h1 class="modal-title h5">Game Registration - ${data[i].schedule}</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                    </div>

                                    <div class="modal-body">                                        
                                        <div class="row mb-3 justify-content-center">
                                            <div class="col-auto">
                                                <img src="images/gcash.png" height="100"/>
                                            </div>
                                            <div class="col-auto mt-2">
                                                <ul class="list-unstyled" style="font-size: .8rem;">
                                                    <li>Payment accepted thru GCASH only.</li>
                                                    <li>Account: Ralph Paolo Panaligan</li>
                                                    <li>Mobile Number: <input id="gcashNumber" type="hidden" value="09288691098"/>09288691098 <a href="javascript:void(0);" id="copyGcashNumber" data-clipboard-target="#gcashNumber"><i class="bi bi-clipboard-fill"></i></a></li>
                                                    <li>QR Download: <a href="images/cogz-gcash-qr.jpg" id="downloadQR" download="cogz-gcash-qr.jpg"><i class="bi bi-qr-code"></i></a></li>
                                                </ul>
                                            </div>
                                        </div>
                                        <div class="row mb-3 justify-content-center">
                                            <div class="col">
                                                <div class="card">
                                                    <div class="card-body"> 
                                                        <div class="row justify-content-center">
                                                            <div class="col">
                                                                <h6>Additional Registration</h6> 
                                                                <p class="text-xs fst-italic">Name of additional PLAYERS to be registered. Each entry is additional 250 PHP. You can only select verified accounts and not yet registered.</p>
                                                                <div class="col-12">
                                                                    <select id="inputAddUsers${i}" name="user[]" multiple placeholder="Select users..."
                                                                            autocomplete="off">
                                                                    </select>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div> 
                                                </div>
                                            </div>  
                                        </div>
                                        <div class="row mb-3 justify-content-center">
                                            <div class="col">
                                                <div class="card">
                                                    <div class="card-body"> 
                                                        <div class="row justify-content-center">
                                                            <div class="col">
                                                                <h6>Companion List</h6> 
                                                                <p class="text-xs fst-italic">Name of companions (eg. wife, son, etc.) that WILL ONLY accompany you or your group and will NOT play. This is to secure their gate pass entry to our gamesite.</p>
                                                                <button type="button" id="addCompanionButton${i}" class="btn btn-outline-primary">
                                                                    <i class="bi bi-plus-lg"></i>
                                                                </button>
                                                                <ul id="companionList${i}" class="list-unstyled mt-2" style="font-size: .8rem;">
                                                                </ul>
                                                            </div>
                                                        </div>
                                                    </div> 
                                                </div>
                                            </div>  
                                        </div>
                                        <div class="row justify-content-center">
                                            <div class="col">
                                                <div class="card">
                                                    <div class="card-body"> 
                                                        <div class="row mb-3">
                                                            <div class="col">
                                                                <h6>Game Fee Summary</h6>
                                                                <ul class="list-unstyled text-bg-primary p-3" style="font-size: .8rem;">
                                                                    <li>Total Pax: <span id="totalPax${i}" class="badge text-bg-warning">1</span></li>
                                                                    <li>Total Game Fee: <span id="totalGameFee${i}" class="badge text-bg-warning">250 PHP</span></li>
                                                                </ul>
                                                            </div>
                                                        </div> 
                                                        <div class="row mb-3">
                                                            <label for="inputPaymentProof${i}" class="col-sm-12 form-label text-small">Proof of
                                                                Payment</label>
                                                            <div class="col-sm-12">
                                                                <input class="form-control form-control-sm" id="inputPaymentProof${i}" type="file"
                                                                       accept="image/*" oninput="paymentProof${i}.src=window.URL.createObjectURL(this.files[0])" required>
                                                            </div>
                                                        </div>                                        
                                                        <div class="row mb-3 justify-content-center">
                                                            <div class="col-6">
                                                                <img id="paymentProof${i}" alt="" class="img-thumbnail">
                                                            </div>
                                                        </div>
                                                    </div> 
                                                </div>
                                            </div>  
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                                        <button id="sendForVerificationButton${i}" type="button" class="btn btn-outline-primary">
                                            <span id="sendForVerificationSpinner${i}" class="spinner-border spinner-border-sm"></span>
                                            <span class="visually-hidden" role="status">Loading...</span>Send For Verification
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </li>`);

                        const addUsersSelect = new TomSelect("#inputAddUsers" + i, {
                            onItemAdd: function () {
                                this.setTextboxValue('');
                                this.refreshOptions();
                            },
                            maxItems: null,
                            valueField: 'id',
                            labelField: 'title',
                            searchField: 'title'
                        });

                        new ClipboardJS('#copyGcashNumber', {
                            container: document.getElementById('registerGameModal' + i)
                        });

                        if (data[i].status === 'LOCKED') {
                            $("#registerGameButton" + i).addClass("disabled");
                        }

                        let gameDate = new Date(data[i].schedule);
                        gameDate.setHours(0, 0, 0, 0);
                        let advanceDeadline = gameDate.getTime() - (1000 * 60 * 60 * data[i].advanceDeadline);
                        new FlipDown(Math.floor(advanceDeadline / 1000), "timeLeft" + i).start()
                                .ifEnded(() => {
                                    $("#gamestatus" + i).html('<span class="badge text-bg-warning"><i class="bi bi-activity"></i> LOCKED</span>');
                                    $("#registerGameButton" + i).addClass("disabled");
                                    console.log('The countdown has ended!');
                                });

                        for (let j = 0; j < data[i].gameUserList.length; j++) {
                            if (userData.id === data[i].gameUserList[j].userId) {
                                let regStatus = data[i].gameUserList[j].regStatus;
                                if (regStatus === "PAYMENT_VERIFICATION") {
                                    $('#regStatus' + i).html(`<span class="text-disabled fst-italic" style="font-size: .7rem">Payment Verification</span>`);
                                } else {
                                    $('#regStatus' + i).html(`<span class="text-disabled fst-italic" style="font-size: .7rem">Paid and Registered</span>`);
                                }
                            }
                        }

                        $("#registerGameModal" + i).on('show.bs.modal', function (e) {
                            if (!userData.waiverAccepted) {
                                bootbox.alert({
                                    message: '<div class="text-center text-danger">You need to accept the terms of the Gamesite Waiver first.</div>',
                                    size: 'small'

                                }).init(function () {
                                    $('.btn').removeClass('btn-primary');
                                    $('.btn').addClass('btn-outline-primary');
                                });
                                return e.preventDefault();
                            }

                            $("#sendForVerificationSpinner" + i).attr('hidden', true);

                            $.ajax({
                                url: '/game/list-user-candidate-strict',
                                dataType: "json",
                                data: "gameId=" + data[i].id
                            }).done(function (data) {
                                $.each(data, function (i, data) {
                                    let fullname = data.lastname + ", " + data.firstname;
                                    addUsersSelect.addOption({id: data.id, title: fullname});
                                });
                            });
                        });

                        $("#inputAddUsers" + i).on("change", function () {
                            let totalPax = $("#inputAddUsers" + i).val().length + 1;
                            $("#totalPax" + i).text(totalPax);
                            $("#totalGameFee" + i).text((totalPax * 250) + " PHP");
                        });

                        let j = 0;
                        $("#addCompanionButton" + i).on("click", function () {

                            if ($("#companionList" + i).children().length === 0) {
                                $("#companionList" + i).append(`<li id="companionListHeader${i}">
                                    <div class="row">
                                        <div class="col-5">
                                            <span class="text-small">Firstname</span>
                                        </div>
                                        <div class="col-5">
                                            <span class="text-small">Lastname</span>
                                        </div>
                                    </div>
                                </li>`);
                            }

                            $("#companionList" + i).append(`<li id="companionListItem${i + "-" + j}">
                                    <div class="row mb-1">
                                        <div class="col-5">
                                            <input type="text" class="form-control form-control-sm" name="firstname${i}[]" required>
                                        </div>
                                        <div class="col-5">
                                            <input type="text" class="form-control form-control-sm" name="lastname${i}[]" required>
                                        </div>                                        
                                        <div class="col-2">
                                            <button type="button" onclick="\$('#companionListItem${i + "-" + j}').remove(); (\$('#companionList${i}').children().length === 1) && \$('#companionListHeader${i}').remove();" class="btn btn-outline-primary btn-sm">
                                                <i class="bi bi-dash-lg"></i>
                                            </button>
                                        </div>
                                    </div>
                                </li>`);

                            j++;
                        });

                        $("#sendForVerificationButton" + i).on("click", function () {

                            if (!$('#registerGameForm' + i)[0].checkValidity()) {
                                $('#registerGameForm' + i)[0].reportValidity();
                                return;
                            }

                            $("#sendForVerificationButton" + i).prop('disabled', true);
                            $("#sendForVerificationSpinner" + i).attr('hidden', false);

                            let additionalPaxArray = $("#inputAddUsers" + i).val();

                            let firstnameArray = $('input[name="firstname' + i + '[]"]').map(function () {
                                return $(this).val();
                            }).get();

                            let lastnameArray = $('input[name="lastname' + i + '[]"]').map(function () {
                                return $(this).val();
                            }).get();

                            let fd = new FormData();
                            fd.append('paymentProof', $('#inputPaymentProof' + i)[0].files[0]);
                            fd.append('gameId', data[i].id);
                            fd.append('gameSchedule', data[i].schedule);
                            fd.append('gameType', data[i].type);
                            fd.append('additionalPaxArray', additionalPaxArray);
                            fd.append('firstnameArray', firstnameArray);
                            fd.append('lastnameArray', lastnameArray);

                            $.ajax({
                                url: "/user/reg-game",
                                contentType: false,
                                processData: false,
                                type: "post",
                                data: fd
                            }).always(function () {
                                $('#regStatus' + i).html(`<span class="text-disabled fst-italic" style="font-size: .7rem">Payment Verification</span>`);
                                $("#registerGameModal" + i).modal("hide");
                                $("#sendForVerificationButton" + i).prop('disabled', false);
                                stompSendAction('PAYMENT_VERIFICATION');
                            });
                        });

                        $('#gameBanner' + i).on("error", function () {
                            $('#gameBanner' + i).attr("src", "uploaded-images/banner/default-banner.png");
                        });
                    }
                });
            } else {
                $("#userStatus").attr({
                    "data-bs-toggle": "tooltip",
                    "data-bs-placement": "bottom",
                    "data-bs-title": "Contact a CoGZ Admin to know your violation details."
                });
                const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
                const tooltipList = [...tooltipTriggerList].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl));

                $("#userStatus").removeClass("text-bg-warning");
                $("#userStatus").addClass("text-bg-danger");
                $("#userStatus").html('<i class="bi bi-x-circle"></i> BANNED');
                $("#gamelist").html(`<div class="text-center">Nothing to show</div>`);
            }
        }

        function hasEditChanges() {
            if (userData.firstname !== $('#editInputFirstname').val())
                return true;
            if (userData.lastname !== $('#editInputLastname').val())
                return true;
            if (userData.callsign !== $('#editInputCallsign').val())
                return true;
            if (userData.email !== $('#editInputEmail').val())
                return true;
            if (userData.mobileNumber !== $('#editInputMobileNumber').val())
                return true;
            if (userData.birthdate !== $('#editInputBirthdate').val())
                return true;
            if ($('#editInputValidId')[0].files[0] !== undefined)
                return true;
            return false;
        }

        stompClient.onConnect = (frame) => {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/topic/client-update', (msgJson) => {
                let actionData = JSON.parse(msgJson.body);

                if (actionData.userId !== userData.id) {
                    return;
                }

                switch (actionData.action) {
                    case 'ACCOUNT_GOOD':
                        userData.status = 'GOOD';
                        setUserStatus();
                        break;
                    case 'ACCOUNT_BANNED':
                        userData.status = 'BANNED';
                        setUserStatus();
                        break;
                    case 'MODIFICATION_APPROVED':
                        setUserData();
                        break;
                    case 'PAYMENT_VERIFIED':
                        setUserStatus();
                        break;
                }
            });
        };

        function stompSendAction(action) {
            stompClient.publish({
                destination: "/app/client-action",
                body: JSON.stringify({userId: userData.id, action: action})
            });
        }
    }
}