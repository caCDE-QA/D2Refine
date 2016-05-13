function D2RefineAboutDialog() {}

D2RefineAboutDialog.prototype = {
  init: function () {
    this.dialogElement1 = $(DOM.loadHTML("d2refine", "dialogs/d2rAbout.html"));
    var controls = DOM.bind(this.dialogElement1);
    controls.close.click(this.boundTo("hide"));
  },
  
  show: function () {
    this.init();
    this.dialogLevel1 = DialogSystem.showDialog(this.dialogElement1);
  },
  
  hide: function () {
    DialogSystem.dismissUntil(this.dialogLevel1 - 1);
  },
};
