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

            changeToWaiverAccepted(data.waiverAccepted);

            $("#editInputUsername").val(data.username);
            $("#editInputFirstname").val(data.firstname);
            $("#editInputLastname").val(data.lastname);
            $("#editInputEmail").val(data.email);
            $("#editInputMobileNumber").val(data.mobileNumber);
            $("#editInputBirthdate").val(data.birthdate);
        });

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
                                src="uploaded-images/banner/12.jpg">
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
                                   <li class="mt-2">
                                       <button id="reg${i}" type="button" class="btn btn-outline-primary"
                                               data-bs-toggle="modal"
                                               data-bs-target="#showRegisterModal">Register
                                       </button>
                                   </li>
                               </ul>
                           </div>
                       </div>
                    </div>
                    </li>`);
                let gameDate = new Date(data[i].schedule);
                new FlipDown(Math.floor(gameDate.getTime() / 1000), "timeLeft" + [i]).start()
                        .ifEnded(() => {
                            $("#gamestatus" + i).html('<span class="badge text-bg-warning"><i class="bi bi-activity"></i> LOCKED</span>');
                            $("#reg" + i).addClass("disabled");
                            console.log('The countdown has ended!');
                        });
            }
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

        $("#acceptWaiverButton").on("click", function () {
            $.ajax({
                url: "/user/accept-waiver",
                type: "post",
                async: false
            }).always(function () {
                $("#waiverModal").modal("hide");
                changeToWaiverAccepted(true);
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
    }
}