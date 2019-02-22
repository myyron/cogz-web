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
 * @fileOverview The player detail page class.
 * @author Myyron Latorilla
 */
class PlayerPage extends DetailPage {

    constructor() {
        let pageName = 'player';
        let playerDto = $.parseJSON($('#player-dto').text());
        let tableDetail = [
            {
                name: 'gun',
                data: playerDto.guns,
                columns: [
                    {data: 'name'},
                    {data: 'model'},
                    {data: 'gunType'}
                ]
            }
        ];
        super(pageName, tableDetail, playerDto.callSign);
        this._playerDto = playerDto;
        this._gunTableIndex = 0;
        this._initEvents(this);
        this._loadDetail();
    }

    _initEvents(self) {
        $('#btn-edit-' + this._pageName).click(function () {
            $('#input-edit-id').val(self._playerDto.id);
            $('#input-edit-call-sign').val(self._playerDto.callSign);
            $('#input-edit-first-name').val(self._playerDto.firstName);
            $('#input-edit-last-name').val(self._playerDto.lastName);
            $('#input-edit-contact-num').val(self._playerDto.contactNum);
            $('#modal-edit-' + self._pageName).modal('show');
        });

        $('#btn-edit-' + this._pageName + '-save').click(function () {
            self._ajaxDetailUpdate('edit', self, undefined, function (resultData) {
                self._playerDto = $.parseJSON(resultData);
                self._loadDetail();
            });
        });

        $('#btn-add-gun-' + this._pageName + '-save').click(function () {
            $('#input-add-gun-player-id').val(self._playerDto.id);
            self._ajaxDetailListUpdate('add-gun', self, 'gun', undefined, function (resultData) {
                self._table[self._gunTableIndex].row.add($.parseJSON(resultData)).draw();
            });
        });

        $('#btn-edit-gun-' + this._pageName).click(function () {
            let selectedData = self._table[self._gunTableIndex].row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                $('#input-edit-gun-player-id').val(self._playerDto.id);
                $('#input-edit-gun-id').val(selectedData.id);
                $('#input-edit-gun-name').val(selectedData.name);
                $('#input-edit-gun-model').val(selectedData.model);
                $('#input-edit-gun-type').val(selectedData.gunType);
                $('#modal-edit-gun-' + self._pageName).modal('show');
            }
        });

        $('#btn-delete-gun-' + this._pageName).click(function () {
            let selectedData = self._table[self._gunTableIndex].row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                Dialog.alertDelete(function () {
                    self._ajaxDetailListUpdate('delete-gun', self, 'gun'
                            , {playerId: self._playerDto.id, gunId: selectedData.id}, function () {
                        let dataIndex = -1;
                        self._table[self._gunTableIndex].row(function (idx, data) {
                            if (data.id === selectedData.id) {
                                dataIndex = idx;
                            }
                            return false;
                        });
                        self._table[self._gunTableIndex].row(dataIndex).remove().draw();
                    });
                });
            }
        });

        $('#btn-edit-gun-' + this._pageName + '-save').click(function () {
            self._ajaxDetailListUpdate('edit-gun', self, 'gun', undefined, function (resultData) {
                let resultJSON = $.parseJSON(resultData);
                let dataIndex = -1;
                self._table[self._gunTableIndex].row(function (idx, data) {
                    if (data.id == resultJSON.id) {
                        dataIndex = idx;
                    }
                    return false;
                });
                self._table[self._gunTableIndex].row(dataIndex).data(resultJSON).draw();
            });
        });
    }

    _loadDetail() {
        $('#call-sign').val(this._playerDto.callSign);
        $('#first-name').val(this._playerDto.firstName);
        $('#last-name').val(this._playerDto.lastName);
        $('#contact-num').val(this._playerDto.contactNum);
    }

}
