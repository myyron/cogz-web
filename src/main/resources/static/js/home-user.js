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

        let userId;
        let userStatus;
        let waiverAccepted;

        $("#navHome").addClass("active");
        $("#navTools").removeClass("active");

        $.ajax({
            url: "/user/current"
        }).done(function (data) {

            userId = data.id;
            waiverAccepted = data.waiverAccepted;
            userStatus = data.status;

            $("#profilePic").attr("src", "uploaded-images/profile/" + userId + ".jpg");

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
            $("#editInputEmail").val(data.email);
            $("#editInputMobileNumber").val(data.mobileNumber);
            $("#editInputBirthdate").val(data.birthdate);

            $("#resetPasswordUserId").val(data.id);
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
                async: false,
                data: fd
            }).always(function () {
                $("#profilePic").attr("src", "uploaded-images/profile/" + userId + ".jpg");
            });
        });

        $('#waiverModal').on('show.bs.modal', function () {
            if (waiverAccepted) {
                $('#waiverModalFooter').html(`<span class="text-disabled fst-italic" style="font-size: .7rem">You already agreed on the terms of the waiver</span>
                    <button type="button" class="btn btn-outline-primary" data-bs-dismiss="modal">Close</button>`);
            } else {
                $('#waiverModalFooter').html(`<button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Skip</button>
                    <button type="submit" class="btn btn-outline-primary">I Agree</button>`);
            }

            $("#waiverForm").on("submit", function () {
                $.ajax({
                    url: "/user/accept-waiver",
                    type: "post",
                    async: false
                }).always(function () {
                    $("#waiverModal").modal("hide");
                });
            });
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
            if (userStatus === 'ACCOUNT_VERIFICATION') {
                $("#userStatus").addClass("text-bg-warning");
                $("#userStatus").html('<i class="bi bi-question-circle"></i> Account Verification');
                $("#gamelist").append(`<div class="text-center">Nothing to show</div>`);
            } else if (userStatus === 'GOOD') {
                $("#userStatus").addClass("text-bg-success");
                $("#userStatus").html('<i class="bi bi-check-circle"></i> Verified');
                $.ajax({
                    url: "/game/list-active"
                }).done(function (data) {
                    for (let i = 0; i < data.length; i++) {

                        let badgeColor = '<span class="badge text-bg-primary">';
                        if (data[i].status !== "OPEN") {
                            badgeColor = '<span class="badge text-bg-warning">';
                        }

                        $("#gamelist").append(`<li class="list-group-item">
                    <div class="row mb-2">
                       <div class="col">
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
                                   <li id="gamestatus${i}">
                                               ${badgeColor}
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
                                                    <li class="text-primary">Game Fee: 250 PHP</li>
                                                </ul>
                                            </div>
                                        </div>
                                        <div class="row mb-3">
                                            <label for="inputPaymentProof${i}" class="col-sm-12 form-label text-small">Proof of
                                                Payment</label>
                                            <div class="col-sm-12">
                                                <input class="form-control form-control-sm" id="inputPaymentProof${i}" type="file"
                                                       accept="image/*" oninput="paymentProof.src=window.URL.createObjectURL(this.files[0])" required>
                                            </div>
                                        </div>                                        
                                        <div class="row mb-3 justify-content-center">
                                            <div class="col-6">
                                                <img id="paymentProof" alt="" class="img-thumbnail">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Cancel</button>
                                        <button type="submit" class="btn btn-outline-primary">Send For Verification</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    </li>`);

                        new ClipboardJS('#copyGcashNumber', {
                            container: document.getElementById('registerGameModal' + [i])
                        });

                        let gameDate = new Date(data[i].schedule);
                        gameDate.setHours(0, 0, 0, 0);
                        let advanceDeadline = gameDate.getTime() - (1000 * 60 * 60 * data[i].advanceDeadline);
                        new FlipDown(Math.floor(advanceDeadline / 1000), "timeLeft" + [i]).start()
                                .ifEnded(() => {
                                    $("#gamestatus" + i).html('<span class="badge text-bg-warning"><i class="bi bi-activity"></i> LOCKED</span>');
                                    $("#registerGameButton" + i).addClass("disabled");
                                    console.log('The countdown has ended!');
                                });

                        for (let j = 0; j < data[i].gameUserList.length; j++) {
                            if (userId === data[i].gameUserList[j].userId) {
                                let regStatus = data[i].gameUserList[j].regStatus;
                                if (regStatus === "PAYMENT_VERIFICATION") {
                                    $('#regStatus' + [i]).html(`<span class="text-disabled fst-italic" style="font-size: .7rem">Payment Verification</span>`);
                                } else {
                                    $('#regStatus' + [i]).html(`<span class="text-disabled fst-italic" style="font-size: .7rem">Paid and Registered</span>`);
                                }
                            }
                        }

                        $("#registerGameModal" + [i]).on('show.bs.modal', function (e) {
                            if (!waiverAccepted) {
                                bootbox.alert({
                                    message: '<div class="text-center text-danger">You need to accept the terms of the Gamesite Waiver first.</div>',
                                    size: 'small'

                                }).init(function () {
                                    $('.btn').removeClass('btn-primary');
                                    $('.btn').addClass('btn-outline-primary');
                                });
                                return e.preventDefault();
                            }
                        });

                        $("#registerGameForm" + [i]).on("submit", function () {
                            let fd = new FormData();
                            fd.append('paymentProof', $('#inputPaymentProof' + [i])[0].files[0]);
                            fd.append('gameId', data[i].id);
                            $.ajax({
                                url: "/user/reg-game",
                                contentType: false,
                                processData: false,
                                type: "post",
                                async: false,
                                data: fd
                            }).always(function () {
                                $("#registerGameModal" + [i]).modal("hide");
                            });
                        });

                        $('#gameBanner' + [i]).on("error", function () {
                            $('#gameBanner' + [i]).attr("src", "uploaded-images/banner/default-banner.png");
                        });
                    }
                });
            } else {
                $("#userStatus").addClass("text-bg-danger");
                $("#userStatus").html('<i class="bi bi-x-circle"></i> BANNED');
                $("#gamelist").append(`<div class="text-center">Nothing to show</div>`);
            }
        }
    }
}