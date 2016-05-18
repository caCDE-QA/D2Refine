/* Expose configuration */
//var D2RefineExtension = {};
//
// Register a dummy reconciliation service that will be used to display named entities
//ReconciliationManager.registerService({
//  name: "D2Refine",
//  url: "d2refine",
//  ui: {"handler" : "D2RefineReconPanel"},
//});

var TermReconciliationManager = {};

TermReconciliationManager.registerService = function(data,level){
    if (data.code === "error"){
            alert('Error: ' + data.message);
    }else{
            var url = location.href;
            var baseURL = url.substring(0,url.lastIndexOf('/'));
            var service_url = baseURL + '/extension/d2refine/services/' + data.service;
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

TermReconciliationManager.synchronizeServices = function(onDone){
    var services = ReconciliationManager.getAllServices();
    var ids = [];
    for(var i=0;i<services.length;i++){
            if(services[i].url){
                    ids.push(services[i].url);
            }
    }
    // This call invokes the Registration command to register the D2Refine Services
    $.post("command/d2refine/registerD2RefineServices",{"services":JSON.stringify(ids)},function(data){
        TermReconciliationManager.registerService(data);
            if(onDone){
                    onDone();
            }
    },"json");
};

TermReconciliationManager.synchronizeServices();