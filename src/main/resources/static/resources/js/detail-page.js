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

    constructor(pageName, tableDetail) {
        super(pageName);
        this._tableDetail = tableDetail;
        this._table = this._initTable();
    }

    _initTable() {
        let result = [];
        for (let i = 0; i < this._tableDetail.length; i++) {
            result[i] = $('#dt-' + this._tableDetail[i].name).DataTable({
                data: this._tableDetail[i].data,
                columns: this._tableDetail[i].columns,
                select: {
                    style: 'single'
                }
            });
        }
        return result;
    }

}
