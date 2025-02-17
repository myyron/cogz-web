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

class Tools {

    constructor() {

        let limitJoule;
        let limitFps;

        $("#navHome").removeClass("active");
        $("#navGameList").removeClass("active");
        $("#navUserList").removeClass("active");
        $("#navTools").addClass("active");

        $("#airsoftType").on("change", function () {

            let val = $("#airsoftType :selected").val();

            if (val === '1') {
                limitJoule = 1.881;
                limitFps = 450;
            } else {
                limitJoule = 2.810;
                limitFps = 550;
            }

            if (val) {
                $("#baselineBbWeight").val("");
                $("#baselineFps").val("");
                $("#baselineJoule").val("");
                $("#endlineBbWeight").val("");
                $("#endlineFps").val("");
                $("#endlineJoule").val("");

                $("#baselineBbWeight").prop("disabled", false);
                $("#baselineFps").prop("disabled", false);
                $("#endlineBbWeight").prop("disabled", false);
                $("#endlineFps").prop("disabled", false);
            }
        });

        $("#baselineBbWeight").on("keyup", function () {
            let bbWeight = $(this).val();
            let fps = $("#baselineFps").val();
            if (fps) {
                $("#baselineJoule").val(computeJoules(bbWeight, fps, "baselineJoule"));
                computeJouleCreep();
            }
        });

        $("#baselineFps").on("keyup", function () {
            let fps = $(this).val();
            let bbWeight = $("#baselineBbWeight").val();
            if (fps) {
                $("#baselineJoule").val(computeJoules(bbWeight, fps, "baselineJoule"));
                computeJouleCreep();
            }
        });

        $("#endlineBbWeight").on("keyup", function () {
            let bbWeight = $(this).val();
            let fps = $("#endlineFps").val();
            if (fps) {
                $("#endlineJoule").val(computeJoules(bbWeight, fps, "endlineJoule"));
                computeJouleCreep();
            }
        });

        $("#endlineFps").on("keyup", function () {
            let fps = $(this).val();
            let bbWeight = $("#endlineBbWeight").val();
            if (fps) {
                $("#endlineJoule").val(computeJoules(bbWeight, fps, "endlineJoule"));
                computeJouleCreep();
            }
        });

        function computeJoules(bbWeight, fps, jouleId) {

            let mass = bbWeight / 1000;
            let velocity = Math.pow((fps * .3048), 2);
            let joule = .5 * mass * velocity;
            let roundedJoule = round(joule);

            if (roundedJoule > limitJoule) {
                $("#" + jouleId).addClass("text-danger");
            } else {
                $("#" + jouleId).removeClass("text-danger");
            }

            return roundedJoule;
        }

        function computeJouleCreep() {

            let baselineFps = $("#baselineFps").val();
            let baselineJoule = $("#baselineJoule").val();
            let endlineJoule = $("#endlineJoule").val();

            if (!baselineJoule) {
                return;
            }

            if (!endlineJoule) {
                return;
            }

            let difference = endlineJoule - baselineJoule;
            let increase = difference / baselineJoule;
            let newFps = (baselineFps * increase) + parseInt(baselineFps, 10);

            if (newFps > limitFps) {
                $("#newFps").addClass("text-danger");
            } else {
                $("#newFps").removeClass("text-danger");
            }

            $("#difference").text(round(difference));
            $("#increase").text(round(increase * 100));
            $("#newFps").text(round(newFps));
        }

        function round(num) {
            let round = Math.round(num + "e" + 3);
            return Number(round + "e" + -3);
        }
    }
}