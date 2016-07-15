function AddReconciliationServiceDialog() {
    var self = this;
    var dialog = $(DOM.loadHTML("d2refine", "dialogs/d2rAddReconService.html"));
    this._elmts = DOM.bind(dialog);

    var frame = DialogSystem.createDialog();

    frame.width("600px");

    var header = $('<div></div>').addClass("dialog-header").text("Add CTS2 reconciliation service").appendTo(frame);
    var body = $('<div></div>').addClass("dialog-body").append(dialog).appendTo(frame);
    var footer = $('<div></div>').addClass("dialog-footer").appendTo(frame);

    this._level = DialogSystem.showDialog(frame);
    this._footer(footer);
}

AddReconciliationServiceDialog.prototype._footer = function (footer) {
    var self = this;
    $('<button></button>').addClass('button').html("&nbsp;&nbsp;OK&nbsp;&nbsp;").click(function () {
        self._dismissBusy = DialogSystem.showBusy('Adding new CTS2 Reconciliation service');
        var name = self._elmts.service_name.val();
        var cts2BaseUrl = self._elmts.cts2-base-url.val()

        if (name.trim() === "") {
            alert("CTS2 Service Name is required");
            self._dismissBusy();
            return;
        }

        if (cts2BaseUrl.trim() === "") {
            alert("CTS2 Base URL is required");
            self._dismissBusy();
            return;
        }

        TermReconciliationManager.synchronizeServices(
            function () {
                $.post("command/d2refine/addCTS2Service",
                    {
                        "name": name,
                        "url": cts2BaseUrl,
                    },
                    function (data) {
                        self._dismissBusy();
                        TermReconciliationManager.registerService(data, self._level);
                    }, "json");
            }
        );
    }).appendTo(footer);

    $('<button></button>').addClass('button').text("Cancel").click(function () {
        DialogSystem.dismissUntil(self._level - 1);
    }).appendTo(footer);
};