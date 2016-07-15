var TermReconciliationManager = {};

TermReconciliationManager.registerService = function(data,level){
    if (data.code === "error"){
            alert('Error: ' + data.message);
    }else{
            var url = location.href;
            var baseURL = url.substring(0,url.lastIndexOf('/'));
            var service_url = baseURL + '/extension/d2refine/services/' + data.service.id;
            data.service.url = service_url;
            //ReconciliationManager doesnot call this method upon unregister.. this is why I am calling it myself
            ReconciliationManager._rebuildMap();
            
            if(!ReconciliationManager.getServiceFromUrl(service_url)){
                    ReconciliationManager.registerStandardService(data.service.url);
            }
            if(level){
                    DialogSystem.dismissUntil(level - 1);
            }
    }
};

// It takes a function which is executed when registration is a success
TermReconciliationManager.synchronizeServices = function(onDone){
    var services = ReconciliationManager.getAllServices();
    var ids = [];
    for(var i=0;i<services.length;i++){
            if(services[i].url){
                    ids.push(services[i].url);
            }
    }
    // This call invokes the Registration command with all
    // existing Standard Services
    // This invokes RegistrationCommand.doPost() which can validate
    // and send information about the new service, so that it can register
    // via the callback functin here.
    $.post("command/d2refine/registerD2RefineServices",
                {"services":JSON.stringify(ids)},
                function(data){
                        TermReconciliationManager.registerService(data);
                        if(onDone){
                            onDone();
                        }
                },"json");
};
// Calling Synchronization Service
TermReconciliationManager.synchronizeServices();