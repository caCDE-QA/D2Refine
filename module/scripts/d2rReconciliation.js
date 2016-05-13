/* Expose configuration */
//var D2RefineExtension = {};
//
// Register a dummy reconciliation service that will be used to display named entities
ReconciliationManager.registerService({
  name: "D2Refine",
  url: "d2refine",
  ui: {"handler" : "D2RefineReconPanel"},
  // By setting the URL to "{{id}}",
  // this whole string will be replaced with the actual URL
  view: { url: "http://localhost:8080/cts2/codesystem/Automobiles/version/1.0/entity/{{id}}?format=json" },
});

var CTS2ReconciliationManager = {};

CTS2ReconciliationManager.registerService = function(data,level){
    if (data.code === "error"){
            alert('Error: ' + data.message);
    }else{
            var url = location.href;  // entire url including querystring - also: window.location.href;
            var baseURL = url.substring(0,url.lastIndexOf('/'));
            var service_url = baseURL + '/extension/d2refine/services/' + data.service.id;
            
            //ReconciliationManager doesnot call this method upon unregister.. this is why I am calling it myself
            ReconciliationManager._rebuildMap();
            
            if(!ReconciliationManager.getServiceFromUrl(service_url)){
                    ReconciliationManager.registerStandardService(service_url);
            }
            if(level){
                    DialogSystem.dismissUntil(level - 1);
            }
    }
};

CTS2ReconciliationManager.synchronizeServices = function(onDone){
    var services = ReconciliationManager.getAllServices();
    var ids = [];
    for(var i=0;i<services.length;i++){
            if(services[i].url){
                    ids.push(services[i].url);
            }
    }
    $.post("command/d2refine/term-reconcile",{"services":JSON.stringify(ids)},function(data){
        CTS2ReconciliationManager.registerService(data);
            if(onDone){
                    onDone();
            }
    },"json");
};

CTS2ReconciliationManager.synchronizeServices();