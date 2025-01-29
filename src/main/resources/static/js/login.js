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

class Login {

    constructor() {

        $("#signupButton").on("click", function () {
            if ($("#signupPassword").val() !== $("#signupPassword2").val()) {
                bootbox.alert({
                    message: '<div class="text-center text-danger">Password do not match</div>',
                    size: 'small'

                }).init(function () {
                    $('.btn').removeClass('btn-primary');
                    $('.btn').addClass('btn-outline-primary');
                });
            } else {

                $.ajax({
                    url: "/api/signup",
                    contentType: "application/json",
                    type: "post",
                    async: false,
                    dataType: "json",
                    data: createDtoFromForm(document.querySelectorAll('#signupForm input'), [{key: 'password', inputId: 'signupPassword'}])
                }).always(function () {
                    $("#loginUsername").val($("#inputUsername").val());
                    $("#signupModal").modal("hide");
                    $("#signupForm")[0].reset();
                });
            }
        });
    }
}