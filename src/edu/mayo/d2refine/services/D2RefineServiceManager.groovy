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
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationResponse
import edu.mayo.d2refine.services.reconciliation.model.SearchResultItem
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

    static final String serviceName = "D2Refine"
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

        ReconciliationService  service = new TermReconciliationService(
                                            id : 'terms',
                                            name : 'CTS2Reconciliation',
                                            refreshContext:false)

        String serviceURL = request.getRequestURL().toString()

        if (path.endsWith("main")) {
            if (!callback) {
                String queries = request.getParameter("queries")
                if (queries){
                    Map<String, ReconciliationRequest> multiQueryRequest =
                            D2rUtils.getMultipleRequest(queries)
                    Map<String, ReconciliationResponse> multiResponse =
                            service.reconcile(multiQueryRequest)
                    return D2rUtils.getMultipleResponse(multiResponse)
                }
            }
        
            getServiceMetadataAsJsonP(service, callback, serviceURL)
        }
        else {
            String prefix = request.getParameter("prefix")
            List<SearchResultItem> results = service.suggestType(prefix)
            D2rUtils.toJSONP(callback, D2rUtils.jsonizeSearchResult(results, prefix))
        }
    }
    
    String getServiceMetadataAsJsonP(ReconciliationService service,
                                     String callback,
                                     String baseServiceUrl) {
        def jsonBuilder = new JsonBuilder()

        jsonBuilder{
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
        
        D2rUtils.toJSONP(callback, jsonBuilder.toString())
    }
}