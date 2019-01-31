class UsersPage {

    constructor() {
        this._userList = $.parseJSON($('#userList').text());
        this._table = this._initUsersTable();
        this._initEvents(this);
    }

    _initEvents(self) {
        $('#btn-create-user').click(function () {
            $('[name=username]').val('');
            $('[name=newPw]').val('');
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
                $('#modal-edit-user').modal('show');
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
            let createUserData = $('#form-create-user').serializeArray()
                    .reduce(function (a, x) {
                        a[x.name] = x.value;
                        return a;
                    }, {});
            $.ajax({
                method: 'POST',
                url: 'create',
                data: {userDto: JSON.stringify(createUserData)}
            })
                    .done(function () {
                        alert('success');
                    })
                    .fail(function (jqXHR) {
                        Dialog.alertError(jqXHR.responseText);
                    });
        });
    }

    _initUsersTable() {
        return $('#dt-users').DataTable({
            data: this._userList,
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
