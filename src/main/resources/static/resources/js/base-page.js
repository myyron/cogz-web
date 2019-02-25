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
 * @fileOverview The base page class.
 * @author Myyron Latorilla
 */
class BasePage {

    constructor(pageName) {
        this._csrfToken = $("meta[name='_csrf']").attr("content");
        this._csrfHeader = $("meta[name='_csrf_header']").attr("content");
        this._pageName = pageName;
        this._init();
    }

    _init() {
        $('.modal').on('hidden.bs.modal', function () {
            $(this).find('form')[0].reset();
        });

        $('input[type="checkbox"]').change(function () {
            this.value = (Number(this.checked));
        });

        $('#datepicker').datepicker({
            autoclose: true
        });
    }

    _getFormData(formId) {
        return $(formId).serializeArray()
                .reduce(function (a, x) {
                    a[x.name] = x.value;
                    return a;
                }, {});
    }

    _getAjaxData(operation, requestParam, oData) {
        let result = {};
        if (typeof oData === 'undefined') {
            if ($('#form-' + operation + '-' + this._pageName).validator('validate').has('.has-error').length) {
                return;
            } else {
                result = {[requestParam]: JSON.stringify(this._getFormData('#form-' + operation + '-' + this._pageName))};
            }
        } else {
            result = oData;
        }
        return result;
    }
}
