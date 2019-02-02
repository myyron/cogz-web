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

        $('#btn-resetpw-user').click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                $('#input-resetpw-username').val(selectedData.username);
                $('#modal-resetpw').modal('show');
            }
        });

        $('#btn-delete-user').click(function () {
            let selectedData = self._table.row('.selected').data();
            if (typeof selectedData === 'undefined') {
                Dialog.alertTableSelect();
            } else {
                Dialog.alertDelete(function () {
                    self._ajaxPost('delete', self, {username: selectedData.username});
                });
            }
        });

        $('#btn-create-save').click(function () {
            self._ajaxPost('create', self);
        });

        $('#btn-edit-save').click(function () {
            self._ajaxPost('edit', self);
        });

        $('#btn-resetpw-save').click(function () {
            self._ajaxPost('resetpw', self);
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
