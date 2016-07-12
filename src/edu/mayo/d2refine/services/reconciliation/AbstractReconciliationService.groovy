package edu.mayo.d2refine.services.reconciliation
import com.google.common.collect.ImmutableMap
import edu.mayo.d2refine.services.model.IF.ServiceType
import edu.mayo.d2refine.model.reconciliation.ReconciliationRequest
import edu.mayo.d2refine.model.reconciliation.ReconciliationResponse
import edu.mayo.d2refine.model.reconciliation.ReconciliationService
import org.apache.commons.lang.StringUtils
import org.json.JSONException
import org.json.JSONWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.Map.Entry

abstract class AbstractReconciliationService implements ReconciliationService
{
        final static Logger logger = LoggerFactory.getLogger("AbstractReconciliationService")

        String name
        String id
        ServiceType serviceType

        public ImmutableMap<String, ReconciliationResponse> reconcile(ImmutableMap<String, ReconciliationRequest> multiQueryRequest)
        {
            Map<String, ReconciliationResponse> multiQueryResponse = new HashMap<String, ReconciliationResponse>();
            for(Entry<String, ReconciliationRequest> entry: multiQueryRequest.entrySet()){
                    try{
                            String key = entry.getKey();
                            ReconciliationRequest request = entry.getValue();
                            
                            if (StringUtils.isBlank(request.getQueryString()))
                                continue;
                            
                            ReconciliationResponse response = reconcile(request);
                            multiQueryResponse.put(key, response);
                            Thread.sleep(300);
                    }catch(Exception e){
                            multiQueryResponse.put(entry.getKey(), new ReconciliationResponse());
                            logger.error("error reconciling '" + entry.getValue().getQueryString() + "'",e);
                    }
            }
            return ImmutableMap.copyOf(multiQueryResponse);
        }

        public void writeAsJson(JSONWriter jsonWriter)
                throws JSONException 
        {
            jsonWriter.object();
            jsonWriter.key("type"); 
            jsonWriter.value(serviceType);
            
            jsonWriter.key("id");
            jsonWriter.value(id);
            
            jsonWriter.key("name");
            jsonWriter.value(name);
            
            jsonWriter.endObject();        
        }   
}
