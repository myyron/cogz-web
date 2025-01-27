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
        });

        $("#inputProfilePic").on("change", function () {

            let fd = new FormData();
            fd.append('profilePic', $('#inputProfilePic')[0].files[0]);
            fd.append('userId', userId);

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

        (async function () {
            const data = [
                {date: 20241201, count: 34},
                {date: 20241208, count: 15},
                {date: 20241215, count: 32},
                {date: 20241222, count: 33},
                {date: 20241229, count: 35},
                {date: 20250105, count: 44},
                {date: 20250112, count: 64},
                {date: 20250119, count: 52}
            ];

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
                    labels: data.map(row => row.date),
                    datasets: [{
                            data: data.map(row => row.count)
                        }]
                }
            });
        })();


        function setUserStatus() {
            if (userStatus === 'ACCOUNT_VERIFICATION') {
                $("#userStatus").addClass("text-bg-warning");
                $("#userStatus").html('<i class="bi bi-question-circle"></i> Account Verification');
            } else if (userStatus === 'GOOD') {
                $("#userStatus").addClass("text-bg-success");
                $("#userStatus").html('<i class="bi bi-check-circle"></i> Verified');
            } else {
                $("#userStatus").addClass("text-bg-danger");
                $("#userStatus").html('<i class="bi bi-x-circle"></i> BANNED');
            }
        }
    }
}