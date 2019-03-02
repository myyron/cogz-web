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
 * @fileOverview The game detail page class.
 * @author Myyron Latorilla
 */
class GamePage extends DetailPage {

    constructor() {
        let pageName = 'game';
        let game = $.parseJSON($('#game').text());
        let tableDetail = [
            {
                name: 'player',
                data: game.playerList,
                columns: [
                    {data: 'player.callSign'},
                    {data: null},
                    {data: 'player.totalFee'},
                    {data: 'player.checkOut'}
                ]
            },
            {
                name: 'fee',
                data: game.feeSummaryList,
                columns: [
                    {data: 'feeSummary.itemName'},
                    {data: 'feeSummay.quantity'},
                    {data: 'feeSummay.totalFee'}
                ]
            },
            {
                name: 'expense',
                data: game.gameExpenseList,
                columns: [
                    {data: 'itemName'},
                    {data: 'totalCost'}
                ]
            }
        ];
        super(pageName, tableDetail, game.date);
        this._game = game;
        this._playerTableIndex = 0;
        this._feeTableIndex = 1;
        this._expenseTableIndex = 2;
        this._initEvents(this);
        this._loadDetail();
    }

    _initEvents(self) {
        $('#btn-edit-' + this._pageName).click(function () {
            $('#input-edit-id').val(self._player.id);
            $('#input-edit-call-sign').val(self._player.callSign);
            $('#input-edit-first-name').val(self._player.firstName);
            $('#input-edit-last-name').val(self._player.lastName);
            $('#input-edit-contact-num').val(self._player.contactNum);
            $('#modal-edit-' + self._pageName).modal('show');
        });

        $('#btn-edit-' + this._pageName + '-save').click(function () {
            self._ajaxDetailUpdate('edit', self, undefined, function (resultData) {
                self._player = $.parseJSON(resultData);
                self._loadDetail();
            });
        });

        $('#btn-add-expense-' + this._pageName + '-save').click(function () {
            $('#input-add-expense-game-id').val(self._game.id);
            self._ajaxDetailListUpdate('add-expense', self, 'gameExpense', undefined, function (resultData) {
                self._table[self._expenseTableIndex].row.add($.parseJSON(resultData)).draw();
            });
        });

        $('#btn-edit-gun-' + this._pageName).click(function () {
            let selectedData = self._table[self._gunTableIndex].row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                $('#input-edit-gun-player-id').val(self._player.id);
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
                            , {playerId: self._player.id, gunId: selectedData.id}, function () {
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
        $('#date').val(this._game.date);
        $('#event-desc').val(this._game.eventDesc);
        $('#total-players').val(this._game.totalPlayers);
        $('#game-status').val(this._game.gameStatus);
    }
}
