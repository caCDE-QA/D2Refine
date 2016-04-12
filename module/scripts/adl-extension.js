/* Add menu to extension bar */
ExtensionBar.addExtensionMenu({
  id: "D2Refine",
  label: "D2Refine",
  submenu: [
    {
      id   : "configuration",
      label: "Configure...",
      click: dialogHandler(ConfigurationDialog),
    },
    { /* separator */ },
    {
      id   : "about",
      label: "About...",
      click: dialogHandler(D2RAboutDialog),
    },
  ]
});

function dialogHandler(dialogConstructor) {
  var dialogArguments = Array.prototype.slice.call(arguments, 1);
  function Dialog() { return dialogConstructor.apply(this, dialogArguments); }
  Dialog.prototype = dialogConstructor.prototype;
  return function () { new Dialog().show(); };
}