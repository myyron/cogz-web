class Dialog {

    static alertTableSelect() {
        bootbox.alert({
            message: '<i class="fa fa-info-circle text-info"></i> Select from the table first.',
            size: 'small'
        });
    }

    static alertDelete() {
        bootbox.confirm({
            message: '<i class="fa fa-exclamation-triangle text-warning"></i> Confirm delete operation.',
            size: 'small',
            callback: function (result) {
                if (result) {
                    alert(result);
                }
            }
        });
    }

    static alertError(message) {
        bootbox.alert({
            title: '<i class="fa fa-times text-danger"></i> System Error',
            message: message
        });
    }
}
