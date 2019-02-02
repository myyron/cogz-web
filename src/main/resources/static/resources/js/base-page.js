class BasePage {

    constructor() {
        this._csrfToken = $("meta[name='_csrf']").attr("content");
        this._csrfHeader = $("meta[name='_csrf_header']").attr("content");
        this._init();
    }

    _init() {
        $('.modal').on('hidden.bs.modal', function () {
            $(this).find('form')[0].reset();
        });
    }

    _getFormData(formId) {
        return $(formId).serializeArray()
                .reduce(function (a, x) {
                    a[x.name] = x.value;
                    return a;
                }, {});
    }

    _ajaxPost(operation, self, oData) {

        let data = {};
        if (typeof oData === 'undefined') {
            data = {userDto: JSON.stringify(this._getFormData('#form-' + operation))};
        } else {
            data = oData;
        }

        $.ajax({
            method: 'POST',
            url: operation,
            data: data,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(self._csrfHeader, self._csrfToken);
            }
        })
                .done(function () {
                    $('#modal-' + operation).modal('hide');
                    $('#dt').DataTable().ajax.reload();
                })
                .fail(function (jqXHR) {
                    Dialog.alertError(jqXHR.responseText);
                });
    }
}
