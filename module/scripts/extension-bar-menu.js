/* Add menu to extension bar */
ExtensionBar.addExtensionMenu({
  id: "d2Refine",
  label: "D2Refine",
  submenu: [
    {
      id   : "d2Refine/about",
      label: "About D2Refine...",
      click: function(){
          var ad = new D2RefineAboutDialog();
          ad.show();
      }
    },
  ]
});