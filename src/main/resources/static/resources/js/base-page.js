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

        $('input[type="checkbox"]').change(function () {
            this.value = (Number(this.checked));
        });
    }

    _getFormData(formId) {
        return $(formId).serializeArray()
                .reduce(function (a, x) {
                    a[x.name] = x.value;
                    return a;
                }, {});
    }

    _ajaxPost(operation, self, retainModal, oData) {

        let data = {};
        if (typeof oData === 'undefined') {
            if ($('#form-' + operation + '-' + this._pageName).validator('validate').has('.has-error').length) {
                return;
            } else {
                data = {[this._pageName + 'Dto']: JSON.stringify(this._getFormData('#form-' + operation + '-' + this._pageName))};
            }
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
                    if (retainModal) {
                        $('#form-' + operation + '-' + self._pageName)[0].reset();
                    } else {
                        $('#modal-' + operation + '-' + self._pageName).modal('hide');
                    }
                    $('#dt-' + self._pageName).DataTable().ajax.reload();
                })
                .fail(function (jqXHR) {
                    Dialog.alertError(jqXHR.responseText);
                });
    }
}
