/*
 Copyright (c) 2016, Mayo Clinic
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 Neither the name of the <ORGANIZATION> nor the names of its contributors
 may be used to endorse or promote products derived from this software
 without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/*
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
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