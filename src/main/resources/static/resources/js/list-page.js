class ListPage extends BasePage {

    constructor(pageName, columns) {
        super();
        this._pageName = pageName;
        this._columns = columns;
        this._table = this._initUsersTable();
    }

    _initUsersTable() {
        return $('#dt-' + this._pageName).DataTable({
            ajax: {
                url: 'list',
                dataSrc: ''
            },
            columns: this._columns,
            select: {
                style: 'single'
            }
        });
    }

}
