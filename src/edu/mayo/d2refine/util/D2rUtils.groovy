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

package edu.mayo.d2refine.util

import edu.mayo.d2refine.services.reconciliation.model.ReconciliationCandidate
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationResponse
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
/**
 *  Common Utility Methods
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */

public class D2rUtils
{
    static String getIdForString(String name)
    {
        name?.toLowerCase()?.replaceAll("\\s+", "-")?.replaceAll("[^-.a-zA-Z0-9]", "")?.replaceAll("\\-\\-+", "-");
    }
    
    static String toJSONP(String callback, String obj)
    {
        "${callback}(${obj})"
    }
    
    static String jsonizeSearchResult(List<ReconciliationCandidate> results, String pref)
    {
        def jsonBuilder = new JsonBuilder()

        jsonBuilder {
            code: '/api/status/ok'
            status: '200 OK'
            prefix: pref
            result: {
                results.collect {item ->
                    id: item.id
                    name: item.name
                    type: {
                        id: item.id
                        name: item.id
                    }
                }
            }
        }

        jsonBuilder.toString()
    }

    static String getMultipleResponse(Map<String,ReconciliationResponse> multiResponse)
    {
        // This method creates the response using JSONBuilder.
        // But at the end we have to replace "results" and "types" into thier singular forms as required by OpenRefine.
        // Find out easy way before using this instead of Jackson library for JSON Object.
        def data = [
                keys: multiResponse.each { k, v ->
                    k:
                    [
                        v.results?.collect {
                            ['id' : it.id,
                            'name' : it.name,
                            'type' : it.types as List,
                            'score' : it.score,
                            'match' : it.match ]
                        },
                    ]
                }
            ]

        def jbl = new JsonBuilder(data.values()?.first())

        String val = jbl.toString()
        val?.replaceAll("results", "result").replaceAll("types", "type")
    }

    static ReconciliationResponse wrapCandidates(List<? extends ReconciliationCandidate> candidates)
    {
        ReconciliationResponse response = new ReconciliationResponse()
        response.results = candidates
        return response;
    }

    static Map<String, ReconciliationRequest> getMultipleRequest(String queries){
            Map multiRequest = [:]
            JsonSlurper slurper = new JsonSlurper()
            def jsonQueries = slurper.parseText(queries)
            jsonQueries.each {k,v ->
                if (v) {
                    //def js = slurper.parseText(v)
                    ReconciliationRequest req
                    if (v.query) {
                        def resultLimit = (v.type)?D2RC.DEFAULT_RESULT_LIMIT:D2RC.MAXIMUM_RESULT_LIMIT

                        req = new ReconciliationRequest(queryString: v.query, limit: resultLimit)

                        if (v.type)
                            req.types = v.type as String[]

                        if (v.properties)
                            req.context = v.properties
                    }
                    multiRequest.put(k, req)
                }
            }
            multiRequest
    }
}
