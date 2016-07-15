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
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationCandidate
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationResponse
import edu.mayo.d2refine.services.reconciliation.model.SearchResultItem
import groovy.json.JsonBuilder
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.JsonParseException
import org.codehaus.jackson.map.JsonMappingException
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.ArrayNode
import org.codehaus.jackson.node.ObjectNode

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
    
    static String jsonizeSearchResult(ImmutableList<SearchResultItem> results, String pref)
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
                        name: item.name
                    }
                }
            }
        }

        jsonBuilder.toString()
    }

    static String getMultipleResponseWithJSONBuilder(ImmutableMap<String,ReconciliationResponse> multiResponse)
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

    static String getMultipleResponse(ImmutableMap<String,ReconciliationResponse> multiResponse)
    {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode multiResponseObj = mapper.createObjectNode();
//        for(Entry<String, ReconciliationResponse> entry: multiResponse.entrySet())
//        {
//                String key = entry.getKey();
//                ReconciliationResponse response  = entry.getValue();
//                multiResponseObj.put(key, getResponse(response));
//        }

        // if this fails in returning correct values - uncomment other lines in this method
        String jb = getMultipleResponseWithJSONBuilder(multiResponse)

//        def map1 = new JsonSlurper().parseText(jb)
//        def map2 = new JsonSlurper().parseText(multiResponseObj.toString())
//
//        boolean same = (map1 == map2)
//
//        return multiResponseObj.toString();
    }

    @Deprecated
    static ObjectNode getResponse(ReconciliationResponse response)
    {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseObj = mapper.createObjectNode();
        ArrayNode resultArr = mapper.createArrayNode();
        for(ReconciliationCandidate result:response.getResults()){
                ObjectNode resultItemObj = getResultItem(result);
                resultArr.add(resultItemObj);
        }
        responseObj.put("result", resultArr);

        return responseObj;

    }

    static ReconciliationResponse wrapCandidates(List<? extends ReconciliationCandidate> candidates)
    {
        ReconciliationResponse response = new ReconciliationResponse();
        response.setResults(candidates);
        return response;
    }

    @Deprecated
    static ObjectNode getResultItem(ReconciliationCandidate item)
    {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultItemObj = mapper.createObjectNode();
        resultItemObj.put("id", item.getId());
        resultItemObj.put("name", item.getName());
        resultItemObj.put("score", item.getScore());
        resultItemObj.put("match", item.isMatch());

        ArrayNode typesArr = mapper.createArrayNode();
        for(int i=0;i<item.getTypes().length;i++){
                String id = item.getTypes()[i];
                //int index = D2rUtils.getNamespaceEndPosition(id);
                //String prefix = prefixManager.getPrefix(id.substring(0,index));
                ObjectNode typeObj = mapper.createObjectNode();
                typeObj.put("id", id);
                //if(prefix!=null){
                //        String localName = id.substring(index);
                //        typeObj.put("name", prefix +":" + localName);
                //}else{
                        typeObj.put("name", id);
                //}
                typesArr.add(typeObj);
        }
        resultItemObj.put("type", typesArr);

        return resultItemObj;
    }
    
    static ImmutableMap<String, ReconciliationRequest> getMultipleRequest(String queries)
                                    throws JsonParseException, JsonMappingException, IOException
    {
            Map<String, ReconciliationRequest> multiRequest = new HashMap<String, ReconciliationRequest>();
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readValue(queries, JsonNode.class);
            Iterator<String> keysIter = root.getFieldNames();
            while(keysIter.hasNext()){
                    String key = keysIter.next();
                    //FIXME parsed twice 
                    ReconciliationRequest request = ReconciliationRequest.valueOf(root.path(key).toString());
                    multiRequest.put(key, request);
            }
            
            return ImmutableMap.copyOf(multiRequest);
    }
}
