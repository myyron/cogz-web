class PlayersPage extends ListPage {

    constructor() {
        let pageName = 'player';
        let columns = [
            {data: null,
                render: function (data) {
                    return '<a href="/profiles/player/' + data.id + '">' + data.callSign + '</a>';
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
        this._pageName = pageName;
        this._initEvents(this);
    }

    _initEvents(self) {
        $('#btn-delete-' + this._pageName).click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                Dialog.alertDelete(function () {
                    self._ajaxPost('delete', self, false, {itemName: selectedData.itemName});
                });
            }
        });

        $('#btn-create-save').click(function () {
            self._ajaxPost('create', self);
        });

        $('#btn-create-save-add').click(function () {
            self._ajaxPost('create', self, true);
        });
    }

}
