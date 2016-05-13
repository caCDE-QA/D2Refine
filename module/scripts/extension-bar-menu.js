/* Add menu to extension bar */
ExtensionBar.addExtensionMenu({
  id: "d2refine",
  label: "D2Refine",
  submenu: [
    {
      id   : "d2refine/d2rAbout",
      label: "About D2Refine...",
      click: function(){
          var aboutDl = new D2RefineAboutDialog();
          //var dlg = aboutDl.prototype;
          aboutDl.show();
      }
    },
  ]
});