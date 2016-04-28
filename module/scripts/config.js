/* Expose configuration */
var D2RefineExtension = {};

ReconciliationManager.registerService({
    name: "D2refine",
    url: "extension/d2refine",
    // By setting the URL to "{{id}}",
    // this whole string will be replaced with the actual URL
    view: { 
        url: "{{id}}" 
          },
  });

//ReconciliationManager.registerService("D2Refine");
//ReconciliationManager._rebuildMap();