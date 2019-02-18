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
 * @fileOverview The users list page class.
 * @author Myyron Latorilla
 */
class UsersPage extends ListPage {

    constructor() {
        let pageName = 'user';
        let columns = [
            {data: 'username'},
            {data: null,
                render: function (data) {
                    return data.firstName + ' ' + data.lastName;
                }
            },
            {data: 'roleType'}
        ];
        super(pageName, columns);
        this._initEvents(this);
    }

    _initEvents(self) {
        $('#btn-create-' + this._pageName).click(function () {
            $('#input-create-username').val('');
            $('#input-create-new-pw').val('');
        });

        $('#btn-edit-' + this._pageName).click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                $('#input-edit-username').val(selectedData.username);
                $('#input-edit-first-name').val(selectedData.firstName);
                $('#input-edit-last-name').val(selectedData.lastName);
                $('#input-edit-role-type').val(selectedData.roleType);
                $('#modal-edit-' + self._pageName).modal('show');
            }
        });

        $('#btn-resetpw-' + this._pageName).click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                $('#input-resetpw-username').val(selectedData.username);
                $('#modal-resetpw-' + self._pageName).modal('show');
            }
        });

        $('#btn-delete-' + this._pageName).click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                Dialog.alertDelete(function () {
                    self._ajaxListUpdate('delete', self, false, {username: selectedData.username});
                });
            }
        });

        $('#btn-create-' + this._pageName + '-save').click(function () {
            self._ajaxListUpdate('create', self);
        });

        $('#btn-edit-' + this._pageName + '-save').click(function () {
            self._ajaxListUpdate('edit', self);
        });

        $('#btn-resetpw-' + this._pageName + '-save').click(function () {
            self._ajaxListUpdate('resetpw', self);
        });
    }

}
