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
 * @fileOverview The base list page class.
 * @author Myyron Latorilla
 */
class ListPage extends BasePage {

    constructor(pageName, columns) {
        super(pageName);
        this._columns = columns;
        this._table = this._initTable();
        this._initListEvents(this);
    }

    _initTable() {
        return $('#dt-' + this._pageName).DataTable({
            ajax: {
                url: 'list',
                dataSrc: ''
            },
            columns: this._columns,
            select: {
                style: 'single'
            }
        });
    }

    _initListEvents(self) {
        $('#btn-delete-' + this._pageName).click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                Dialog.alertDelete(function () {
                    self._ajaxListUpdate('delete', self, false, {id: selectedData.id});
                });
            }
        });

        $('#btn-create-' + this._pageName + '-save').click(function () {
            self._ajaxListUpdate('create', self);
        });
    }

    _ajaxListUpdate(operation, self, retainModal, oData, callback) {
        let data = this._getAjaxData(operation, this._pageName, oData);

        if ($.isEmptyObject(data)) {
            return;
        }

        $.ajax({
            method: 'POST',
            url: operation,
            data: data,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(self._csrfHeader, self._csrfToken);
            }
        })
                .done(function () {
                    if (retainModal) {
                        $('#form-' + operation + '-' + self._pageName)[0].reset();
                    } else {
                        $('#modal-' + operation + '-' + self._pageName).modal('hide');
                    }
                    self._table.ajax.reload();
                    if (typeof callback !== 'undefined') {
                        callback();
                    }
                })
                .fail(function (jqXHR) {
                    Dialog.alertError(jqXHR.responseText);
                });
    }
}
