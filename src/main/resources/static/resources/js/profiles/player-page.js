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
            let gunTableIndex = 0;
            $('#input-add-gun-player-id').val(self._playerDto.id);
            self._ajaxDetailListUpdate('add-gun', self, 'gun', gunTableIndex, undefined, function (resultData) {
                self._table[gunTableIndex].row.add($.parseJSON(resultData)).draw();
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
