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
package edu.mayo.d2refine.services.reconciliation

import edu.mayo.d2refine.services.reconciliation.model.ReconciliationCandidate
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationResponse
import edu.mayo.d2refine.util.D2RC
import edu.mayo.d2refine.util.D2rUtils
import org.json.JSONException
import org.json.JSONWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
abstract class AbstractReconciliationService implements ReconciliationService
{
    final static Logger logger = LoggerFactory.getLogger("AbstractReconciliationService")

    String name
    String id
    D2RC.ServiceType serviceType

    Map<String, ReconciliationResponse> reconcile(
            Map<String, ReconciliationRequest> multiQueryRequest) {

        Map<String, ReconciliationResponse> multiQueryResponse =
                            new HashMap<String, ReconciliationResponse>();

        String[] reconTypes = [D2RC.ServiceType.TERM, D2RC.ServiceType.VALUE_SET]
        boolean match = true
        def typeCandidate = new ReconciliationCandidate(id : 1,
                name: 'Reconciliation Types',
                score: 1.0,
                types: reconTypes,
                match: match)

        try {
            multiQueryRequest?.each { key, reconciliationRequest ->
                if (reconciliationRequest.queryString) {

                    // If types are not supplied in the request
                    // I am assuming that it is just to get the types
                    // and not actual reconciliation request.
                    // TODO: check this logic
                    if (!reconciliationRequest.types) {
                        multiQueryResponse.put(
                                key, D2rUtils.wrapCandidates(
                                [typeCandidate]
                        ))
                    }
                    else {
                        multiQueryResponse.put(
                                key, D2rUtils.wrapCandidates(
                                reconcile(reconciliationRequest)))
                        //Thread.sleep(300);
                    }
                }
            }
        }
        catch(Exception e){
            logger.error("Error Reconciling...",e);
        }

        multiQueryResponse
    }

    void writeAsJson(JSONWriter jsonWriter) throws JSONException {
        jsonWriter.object()
            jsonWriter.key("type"); jsonWriter.value(serviceType)
            jsonWriter.key("id"); jsonWriter.value(id)
            jsonWriter.key("name"); jsonWriter.value(name)
        jsonWriter.endObject();
    }
}
