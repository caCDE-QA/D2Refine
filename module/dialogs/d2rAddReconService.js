/*
 Copyright (c) 2016, Mayo Clinic
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 Neither the name of the <ORGANIZATION> nor the names of its contributors
 may be used to endorse or promote products derived from this software
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */

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