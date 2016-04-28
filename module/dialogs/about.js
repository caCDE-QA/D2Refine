function D2RefineAboutDialog() {}

D2RefineAboutDialog.prototype = {
  init: function () {
    this.dialogElement = $(DOM.loadHTML("d2refine", "dialogs/about.html"));
    var controls = DOM.bind(this.dialogElement);
    controls.close.click(this.bound("hide"));
  },
  
  show: function () {
    this.init();
    this.dialogLevel = DialogSystem.showDialog(this.dialogElement);
  },
  
  hide: function () {
    DialogSystem.dismissUntil(this.dialogLevel - 1);
  },
};
