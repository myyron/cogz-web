/*
 * Copyright 2018 Myyron Latorilla
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
 * 
 * @fileOverview The dialog utility class.
 * @author Myyron Latorilla
 */
class Dialog {

    static alertTableSelect() {
        bootbox.alert({
            message: '<i class="fa fa-info-circle text-info"></i> Select from the table first.',
            size: 'small'
        });
    }

    static alertDelete(callback) {
        bootbox.confirm({
            message: '<i class="fa fa-exclamation-triangle text-warning"></i> Confirm delete operation.',
            size: 'small',
            callback: function (result) {
                if (result) {
                    callback();
                }
            }
        });
    }

    static alertError(message) {
        bootbox.alert({
            title: '<i class="fa fa-times text-danger"></i> System Error',
            message: message
        });
    }
}
