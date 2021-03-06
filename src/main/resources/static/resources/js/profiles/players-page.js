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
 * @fileOverview The players list page class.
 * @author Myyron Latorilla
 */
class PlayersPage extends ListPage {

    constructor() {
        let pageName = 'player';
        let columns = [
            {data: null,
                render: function (data) {
                    return '<a href="/profiles/player/' + data.id + '"><i class="fa fa-edit"></i> ' + data.callSign + '</a>';
                }
            },
            {data: null,
                render: function (data) {
                    return data.firstName + ' ' + data.lastName;
                }
            },
            {data: 'contactNum'}
        ];
        super(pageName, columns);
        this._initEvents(this);
    }

    _initEvents(self) {
        $('#btn-create-' + this._pageName + '-save-add').click(function () {
            self._ajaxListUpdate('create', self, true);
        });
    }
}
