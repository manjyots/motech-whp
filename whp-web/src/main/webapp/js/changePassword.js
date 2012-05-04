$('#changePasswordModal').on('show', function() {
    $('#currentPassword').focus();
    $('#validationError').empty();
    $('#changePasswordModal').find("input").val("");
    $('#validationError').hide();
});

$('#changePasswordModal').submit(function(event) {
    if (!$('#changePasswordModal').valid()) {
        $('#validationError').show();
    }
    else {
        event.preventDefault();
        var $form = $(this), url = $form.attr('action');
        $.post(url, $form.serialize(),
            function(data) {
                if (data == '') {
                    $('#changePasswordModal').modal('hide');
                }
            }
        );
    }
});

$('#changePasswordModal').validate({
    rules: {
        currentPassword: "required",
        newPassword:  {
            required: true,
            minlength: 4,
            notEqualTo: '#currentPassword'
        },
        confirmNewPassword: {
            required: true,
            equalTo: '#newPassword'
        }
    },
    messages: {
        currentPassword: "Please enter 'Current Password'",
        newPassword: {
            required: "Please enter 'New Password'",
            minlength: "'New Password' should at least be 4 characters long",
            notEqualTo: "'New Password' should not be the same as the 'Current Password'"
        },
        confirmNewPassword: {
            required: "Please enter 'Confirm New Password'",
            equalTo: "'Confirm New Password' should match 'New Password'"
        }
    },
    errorPlacement: function(error, element) {
        $('#validationError').append(error);
    },
    errorLabelContainer: '#validationError'
});