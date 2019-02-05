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
                    self._ajaxPost('delete', self, {itemName: selectedData.itemName});
                });
            }
        });

        $('#btn-create-save').click(function () {
            self._ajaxPost('create', self);
        });
    }

}
