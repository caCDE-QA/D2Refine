package edu.mayo.d2refine.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import edu.mayo.d2refine.model.reconciliation.ReconciliationCandidate;

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
    
    public static ObjectNode getJsonReconciliationCandidates(List<ReconciliationCandidate> candidates) 
    {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode multiResponseObj = mapper.createObjectNode();
        
        if (candidates == null)
            return multiResponseObj;
        
        for(ReconciliationCandidate candidate : candidates)
        {
                String key = candidate.getId();
                String value = candidate.getName();
                multiResponseObj.put(key, value);
        }
        
        return multiResponseObj;
    }
}
