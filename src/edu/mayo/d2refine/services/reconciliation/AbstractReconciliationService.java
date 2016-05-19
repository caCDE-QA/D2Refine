package edu.mayo.d2refine.services.reconciliation;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import edu.mayo.d2refine.model.ServiceType;
import edu.mayo.d2refine.model.reconciliation.ReconciliationRequest;
import edu.mayo.d2refine.model.reconciliation.ReconciliationResponse;
import edu.mayo.d2refine.model.reconciliation.ReconciliationService;

public abstract class AbstractReconciliationService implements ReconciliationService
{
        final static Logger logger = LoggerFactory.getLogger("AbstractReconciliationService");
        
        protected String name;
        protected String id;
        protected ServiceType serviceType;
        
        protected AbstractReconciliationService(String id, String name)
        {
                this.name= name;
                this.id = id;
        }
        
        public String getServiceId() 
        {
                return id;
        }

        public String getServiceName() 
        {
                return name;
        }
        
        public ServiceType getServiceType() 
        {
            return serviceType;
        }
        
        
        public void setServiceType(ServiceType serviceType) 
        {
            this.serviceType = serviceType;
        }
        
        public ImmutableMap<String, ReconciliationResponse> reconcile(ImmutableMap<String, ReconciliationRequest> multiQueryRequest) 
        {
            Map<String, ReconciliationResponse> multiQueryResponse = new HashMap<String, ReconciliationResponse>();
            for(Entry<String, ReconciliationRequest> entry: multiQueryRequest.entrySet()){
                    try{
                            String key = entry.getKey();
                            ReconciliationRequest request = entry.getValue();
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
            jsonWriter.value(getServiceType());
            
            jsonWriter.key("id");
            jsonWriter.value(this.getServiceId());
            
            jsonWriter.key("name");
            jsonWriter.value(this.getServiceName());
            
            jsonWriter.endObject();        
        }   
}
