class UsersPage extends BasePage {

    constructor() {
        super();
        this._table = this._initUsersTable();
        this._initEvents(this);
    }

    _initEvents(self) {
        $('#btn-create-user').click(function () {
            $('#input-create-username').val('');
            $('#input-create-newPw').val('');
        });

        $('#btn-edit-user').click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                $('#input-edit-username').val(selectedData.username);
                $('#input-edit-firstname').val(selectedData.firstName);
                $('#input-edit-lastname').val(selectedData.lastName);
                $('#input-edit-role').val(selectedData.roleType);
                $('#modal-edit').modal('show');
            }
        });

        $('#btn-change-pw-admin').click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                $('#input-change-pw-admin-username').val(selectedData.username);
                $('#modal-change-pw-admin').modal('show');
            }
        });

        $('#btn-delete-user').click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                Dialog.alertDelete();
            }
        });

        $('#btn-create-save').click(function () {
            self._ajaxPost('create', self);
        });

        $('#btn-edit-save').click(function () {
            self._ajaxPost('edit', self);
        });
    }

    _initUsersTable() {
        return $('#dt').DataTable({
            ajax: {
                url: 'list',
                dataSrc: ''
            },
            columns: [
                {data: 'username'},
                {data: 'fullName'},
                {data: 'roleType'}
            ],
            select: {
                style: 'single'
            }
        });
    }

}
