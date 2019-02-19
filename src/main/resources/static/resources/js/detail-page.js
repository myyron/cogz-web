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
 * @fileOverview The base detail page class.
 * @author Myyron Latorilla
 */
class DetailPage extends BasePage {

    constructor(pageName, tableDetail, header) {
        super(pageName);
        this._tableDetail = tableDetail;
        this._table = this._initTable();
        $('#header').text(header);
    }

    _initTable() {
        let result = [];
        for (let i = 0; i < this._tableDetail.length; i++) {
            let data = null;
            if (this._tableDetail[i].data.length) {
                data = this._tableDetail[i].data;
            }
            result[i] = $('#dt-' + this._tableDetail[i].name).DataTable({
                data: data,
                columns: this._tableDetail[i].columns,
                select: {
                    style: 'single'
                }
            });
        }
        return result;
    }

    _ajaxDetailUpdate(operation, self, oData, callback) {
        let data = this._getAjaxData(operation, this._pageName, oData);
        $.ajax({
            method: 'POST',
            url: operation,
            data: data,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(self._csrfHeader, self._csrfToken);
            }
        })
                .done(function (resultData) {
                    $('#modal-' + operation + '-' + self._pageName).modal('hide');
                    if (typeof callback !== 'undefined') {
                        callback(resultData);
                    }
                })
                .fail(function (jqXHR) {
                    Dialog.alertError(jqXHR.responseText);
                });
    }

    _ajaxDetailListUpdate(operation, self, subListName, oData, callback) {
        let data = this._getAjaxData(operation, subListName, oData);
        $.ajax({
            method: 'POST',
            url: operation,
            data: data,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(self._csrfHeader, self._csrfToken);
            }
        })
                .done(function (resultData) {
                    $('#modal-' + operation + '-' + self._pageName).modal('hide');
                    if (typeof callback !== 'undefined') {
                        callback(resultData);
                    }
                })
                .fail(function (jqXHR) {
                    Dialog.alertError(jqXHR.responseText);
                });
    }
}
