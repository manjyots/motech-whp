function removeActivateColumnIfAllAreActive() {
    if ($('a[type=activate-button]').length == 0) {
        $('[type=activate-provider]').remove();
    }
}

$(function () {
    $("#district").combobox();
    initializeCollapsiblePane('#search-section', '#search-section-header-link', "Show Search Pane", "Hide Search Pane");

    removeActivateColumnIfAllAreActive();

    $('a[type=activate-button]').click(function () {
        var providerId = $(this).closest('tr').attr('providerId');
        $('#activateProviderUserName').val(providerId);
        $('#activateProviderUserNameLabel').text(providerId);
    });

    $('#activateProviderModal').bind('activateProviderSuccess', function (event, userName) {
        $('tr[providerId=' + userName + '] td[type=activate-provider] a').remove();
        $('tr[providerId=' + userName + '] td[type=status]').text('Active');
        $('tr[providerId=' + userName + ']').effect("highlight", {}, 6000);
        removeActivateColumnIfAllAreActive();
    });
});
