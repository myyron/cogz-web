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
            $('#input-edit-id').val(self._game.id);
            $('#datepicker').val(self._game.date);
            $('#input-edit-event-desc').val(self._game.eventDesc);
            $('#modal-edit-' + self._pageName).modal('show');
        });

        $('#btn-edit-' + this._pageName + '-save').click(function () {
            self._ajaxDetailUpdate('edit', self, undefined, function (resultData) {
                self._game = $.parseJSON(resultData);
                self._loadDetail();
            });
        });

        $('#btn-add-expense-' + this._pageName + '-save').click(function () {
            $('#input-add-expense-game-id').val(self._game.id);
            self._ajaxDetailListUpdate('add-expense', self, 'gameExpense', undefined, function (resultData) {
                self._table[self._expenseTableIndex].row.add($.parseJSON(resultData)).draw();
            });
        });

        $('#btn-edit-expense-' + this._pageName).click(function () {
            let selectedData = self._table[self._expenseTableIndex].row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                $('#input-edit-expense-id').val(selectedData.id);
                $('#input-edit-expense-item-name').val(selectedData.itemName);
                $('#input-edit-expense-total-cost').val(selectedData.totalCost);
                $('#modal-edit-expense-' + self._pageName).modal('show');
            }
        });

        $('#btn-delete-expense-' + this._pageName).click(function () {
            let selectedData = self._table[self._expenseTableIndex].row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                Dialog.alertDelete(function () {
                    self._ajaxDetailListUpdate('delete-expense', self, 'gameExpense'
                            , {id: selectedData.id}, function () {
                        let dataIndex = -1;
                        self._table[self._expenseTableIndex].row(function (idx, data) {
                            if (data.id === selectedData.id) {
                                dataIndex = idx;
                            }
                            return false;
                        });
                        self._table[self._expenseTableIndex].row(dataIndex).remove().draw();
                    });
                });
            }
        });

        $('#btn-edit-expense-' + this._pageName + '-save').click(function () {
            self._ajaxDetailListUpdate('edit-expense', self, 'gameExpense', undefined, function (resultData) {
                let resultJSON = $.parseJSON(resultData);
                let dataIndex = -1;
                self._table[self._expenseTableIndex].row(function (idx, data) {
                    if (data.id == resultJSON.id) {
                        dataIndex = idx;
                    }
                    return false;
                });
                self._table[self._expenseTableIndex].row(dataIndex).data(resultJSON).draw();
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
