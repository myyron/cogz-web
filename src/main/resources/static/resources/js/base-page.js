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

}
