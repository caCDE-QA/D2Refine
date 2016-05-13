/* Expose configuration */
var D2RefineExtension = {};

ReconciliationManager.registerService({
    name: "d2refine",
    url: "command/d2refine/term-reconcile",
    ui: function(){
        var ad = new D2RefineAboutDialog();
        ad.init();
        ad.show();
    },
    // By setting the URL to "{{id}}",
    // this whole string will be replaced with the actual URL
    view: { 
        url: "{{id}}" 
          },
  });

//ReconciliationManager.registerService("D2Refine");
//ReconciliationManager._rebuildMap();