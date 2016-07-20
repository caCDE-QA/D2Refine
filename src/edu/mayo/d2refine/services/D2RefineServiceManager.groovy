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

package edu.mayo.d2refine.services
import edu.mayo.d2refine.services.reconciliation.ReconciliationService
import edu.mayo.d2refine.services.reconciliation.TermReconciliationService
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationCandidate
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationResponse
import edu.mayo.d2refine.util.D2RC
import edu.mayo.d2refine.util.D2rUtils
import groovy.json.JsonBuilder

import javax.servlet.http.HttpServletRequest
/**
 *  D2Refine Service Manager to handle incoming D2Refine Requests.
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
class D2RefineServiceManager {
    static D2RefineServiceManager manager
    
    private D2RefineServiceManager() {}
    /**
     * Create a static method to get instance.
     */
    static D2RefineServiceManager instance() {
        if(!manager)
            manager = new D2RefineServiceManager();
        
        manager;
    }
     
    String handle(String path, HttpServletRequest request) throws IOException {
        // Callback will only be there when service meta data is requested
        String callback = request.getParameter('callback');

        ReconciliationService  termReconciliationService = new TermReconciliationService(
                                            id : D2RC.MAIN_SERVICE_ID,
                                            name : D2RC.MAIN_SERVICE_NAME,
                                            refreshContext:false)

        String serviceURL = request.getRequestURL().toString()


        if (path.endsWith("services/d2refine")) {
            if (callback)
                return getServiceMetadataAsJsonP(D2RC.MAIN_SERVICE_NAME, callback, serviceURL)
            else
                return handleQueries(request, termReconciliationService)
        }
        else if (path.contains(D2RC.REGISTER_CTS2_SERVICE_PREFIX)){
            String customId = (path.split(D2RC.REGISTER_CTS2_SERVICE_PREFIX))[1]
            if (customId.startsWith("/"))
                customId = customId.substring(1)
            // Only split on first ":" - by limiting output array entries to 2
            String[] serviceParts = customId.split(":", 2)
            customId = serviceParts[0]?: D2RC.MAIN_SERVICE_NAME
            String customURL = serviceParts[1]?:""

            String metadata = getServiceMetadataAsJsonP(customId, callback, serviceURL)

            // it means we have some valid metadata created
            // Add or update CTS2 Service with this.
            if (metadata) {
                termReconciliationService.getVocabularyService(null)?.addUpdateRESTBaseServiceURL(customId, customURL)
            }
            if (callback) {
                return metadata
            }
            else{
                termReconciliationService.id = customId
                termReconciliationService.name = customId
                return handleQueries(request, termReconciliationService)
            }
        }
        else if (path.endsWith("services/d2refine/suggest/entity")) {
            String prefix = request.getParameter("prefix")
            List<ReconciliationCandidate> results = termReconciliationService.suggestType(prefix)
            return D2rUtils.toJSONP(callback, D2rUtils.jsonizeSearchResult(results, prefix))
        }
        else if (path.endsWith("services/d2refine/suggest/entity/preview")) {
            return 'test'
        }
        else{
            return handleQueries(request, termReconciliationService)
        }
    }

    static String handleQueries(HttpServletRequest request, TermReconciliationService termReconciliationService){

        Map<String, ReconciliationRequest> multiQueryRequest
        Map<String, ReconciliationResponse> multiResponse

        multiQueryRequest = D2rUtils.getMultipleRequest(request.getParameter("queries")?:"")
        multiResponse = multiQueryRequest?termReconciliationService.reconcile(multiQueryRequest):null
        multiResponse?D2rUtils.getMultipleResponse(multiResponse):null
    }

    String getServiceMetadataAsJsonP(String serviceName,
                                     String callback,
                                     String baseServiceUrl) {
        def jb = new JsonBuilder("")

        jb {
            name serviceName
            schemaSpace D2RC.URI_SPACE
            identifierSpace D2RC.URI_SPACE
            view {
                url baseServiceUrl + '/view?id={{id}}'
            }
            preview {
                url baseServiceUrl + '/preview/template?id={{id}}'
                width 430
                height 300
            }
            suggest {
                type {
                    service_url baseServiceUrl
                    service_path '/suggest/type'
                    flyout_service_url baseServiceUrl
                    flyout_service_path '/suggest/type/preview'
                }
                property {
                    service_url baseServiceUrl
                    service_path '/suggest/property'
                    flyout_service_url baseServiceUrl
                    flyout_service_path '/suggest/property/preview'
                }
                entity {
                    service_url baseServiceUrl
                    service_path '/suggest/entity'
                    flyout_service_url baseServiceUrl
                    flyout_service_path '/suggest/entity/preview'
                }
            }
        }
        
        D2rUtils.toJSONP(callback, jb.toString())
    }
}