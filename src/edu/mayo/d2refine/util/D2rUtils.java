package edu.mayo.d2refine.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.collect.ImmutableMap;

import edu.mayo.d2refine.model.reconciliation.ReconciliationCandidate;
import edu.mayo.d2refine.model.reconciliation.ReconciliationRequest;
import edu.mayo.d2refine.model.reconciliation.ReconciliationResponse;

public class D2rUtils 
{
    public static String getIdForString(String name)
    {
        if (StringUtils.isBlank(name))
            return null;
        
        return name.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^-.a-zA-Z0-9]", "").replaceAll("\\-\\-+", "-");
    }
    
    public static String toJSONP(String callback, ObjectNode obj)
    {
        return callback + "(" + obj + ")";
    }
    
//    public static ObjectNode getJsonReconciliationResponse(ImmutableMap<String, ReconciliationResponse> candidates) 
//    {
//        
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode multiResponseObj = mapper.createObjectNode();
//        for(Entry<String, ReconciliationCandidate> entry: candidates.entrySet()){
//                String key = entry.getKey();
//                ReconciliationCandidate candidate  = entry.getValue();
//                multiResponseObj.put(key, getCandidate(candidate));
//        }
//        
//        return multiResponseObj;
//    }
    
    public static ObjectNode getMultipleResponse(ImmutableMap<String,ReconciliationResponse> multiResponse) 
    {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode multiResponseObj = mapper.createObjectNode();
        for(Entry<String, ReconciliationResponse> entry: multiResponse.entrySet())
        {
                String key = entry.getKey();
                ReconciliationResponse response  = entry.getValue();
                multiResponseObj.put(key, getResponse(response));
        }
        
        return multiResponseObj;
    }
    
    public static ObjectNode getResponse(ReconciliationResponse response) 
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
    
    public static ObjectNode getResultItem(ReconciliationCandidate item)
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
    
    public static ImmutableMap<String, ReconciliationRequest> getMultipleRequest(String queries) 
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
    
//    public static ObjectNode getMultipleCandidates(ImmutableMap<String,ReconciliationCandidate> multiCandidates) 
//    {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode multiCandidateObj = mapper.createObjectNode();
//        for(Entry<String, ReconciliationCandidate> entry: multiCandidates.entrySet()){
//                String key = entry.getKey();
//                ReconciliationCandidate candidate  = entry.getValue();
//                multiCandidateObj.put(key, D2rUtils.getCandidate(candidate));
//        }
//        
//        return multiCandidateObj;
//    }
    
//    public static ObjectNode getCandidate(ReconciliationCandidate candidate) 
//    {
//        ObjectMapper mapper = new ObjectMapper();
//        ObjectNode candidateObj = mapper.createObjectNode();
//        ArrayNode resultArr = mapper.createArrayNode();
//        
//        ObjectNode resultItemObj = getResultItem(candidate);
//        resultArr.add(resultItemObj);
//        candidateObj.put("result", resultArr);
//        
//        return candidateObj;
//    }
    

    
    public static int getNamespaceEndPosition(String uri)
    {
        if(uri.indexOf("#")!=-1){
                return uri.indexOf("#")+1;
        }else{
                return uri.lastIndexOf("/") + 1;
        }
    }

    public static final String URI_SPACE = "http://www.ietf.org/rfc/rfc3986";

}
