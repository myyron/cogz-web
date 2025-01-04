class Account {

    constructor() {
        $.ajax({
            url: "/account/current-user"
        }).done(function (data) {
            $("#inputUsername").val(data.username);
            $("#inputFirstname").val(data.firstname);
            $("#inputLastname").val(data.lastname);
            $("#inputEmail").val(data.email);
            $("#inputMobileNumber").val(data.mobileNumber);
            $("#inputBirthdate").val(data.birthdate);
        });
    }
}