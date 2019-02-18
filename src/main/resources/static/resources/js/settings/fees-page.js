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
 * @fileOverview The fees list page class.
 * @author Myyron Latorilla
 */
class FeesPage extends ListPage {

    constructor() {
        let pageName = 'fee';
        let columns = [
            {data: 'itemName'},
            {data: 'feeType'},
            {data: 'amount'},
            {data: 'refundable',
                render: function (data) {
                    let result = false;
                    if (data) {
                        result = true;
                    }
                    return result;
                }
            }
        ];
        super(pageName, columns);
        this._initEvents(this);
    }

    _initEvents(self) {
        $('#btn-delete-' + this._pageName).click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                Dialog.alertDelete(function () {
                    self._ajaxListUpdate('delete', self, false, {itemName: selectedData.itemName});
                });
            }
        });

        $('#btn-create-' + this._pageName + '-save').click(function () {
            self._ajaxListUpdate('create', self);
        });
    }

}
