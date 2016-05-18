package edu.mayo.d2refine.services;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import edu.mayo.d2refine.model.reconciliation.ReconciliationRequest;
import edu.mayo.d2refine.model.reconciliation.ReconciliationService;
import edu.mayo.d2refine.services.reconciliation.TermReconciliationService;
import edu.mayo.d2refine.util.D2rUtils;

public class D2RefineServiceManager 
{
    private static final String serviceName_ = "D2Refine";
    private static D2RefineServiceManager manager_;
    
    private D2RefineServiceManager() {}
    /**
     * Create a static method to get instance.
     */
    public static D2RefineServiceManager instance()
    {
        if(manager_ == null)
        {
            manager_ = new D2RefineServiceManager();
        }
        
        return manager_;
    }
     
    public String handle(String path, HttpServletRequest request)
    {
        // Callback will only be there when service meta data is requested
        String callback = request.getParameter("callback");
        
        if (StringUtils.isBlank(callback))
        {
            String id = "terms";
            String name = "CTS2Reconciliation";
            
            ReconciliationService  service = new TermReconciliationService(id, name);
            ReconciliationRequest reconRequest = new ReconciliationRequest("chevy", 5);
            return D2rUtils.getJsonReconciliationCandidates(service.reconcile(reconRequest)).toString();
        }
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        
        node.put("name", serviceName_);
                
        return D2rUtils.toJSONP(callback, node);
    }
}