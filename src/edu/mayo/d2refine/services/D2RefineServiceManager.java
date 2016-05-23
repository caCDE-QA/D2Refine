package edu.mayo.d2refine.services;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import edu.mayo.d2refine.model.reconciliation.ReconciliationRequest;
import edu.mayo.d2refine.model.reconciliation.ReconciliationResponse;
import edu.mayo.d2refine.model.reconciliation.ReconciliationService;
import edu.mayo.d2refine.model.reconciliation.SearchResultItem;
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
     
    public String handle(String path, HttpServletRequest request) throws JsonParseException, JsonMappingException, IOException
    {
        // Callback will only be there when service meta data is requested
        String callback = request.getParameter("callback");
        
        String id = "terms";
        String name = "CTS2Reconciliation";
        ReconciliationService  service = new TermReconciliationService(id, name);
        String serviceURL = request.getRequestURL().toString();
        
        if (path.endsWith("main"))
        {        
            if (StringUtils.isBlank(callback))
            {
                String queries = request.getParameter("queries");
                ImmutableMap<String, ReconciliationRequest> multiQueryRequest = D2rUtils.getMultipleRequest(queries);
                ImmutableMap<String, ReconciliationResponse> multiResponse = service.reconcile(multiQueryRequest);
                return D2rUtils.getMultipleResponse(multiResponse).toString();
            }
        
            return getServiceMetadataAsJsonP(service, callback, serviceURL);
        }
        else
        {
            String prefix = request.getParameter("prefix");
            ImmutableList<SearchResultItem> results = service.suggestType(prefix);
            return D2rUtils.toJSONP(callback, D2rUtils.jsonizeSearchResult(results, prefix));
        }
    }
    
    public String getServiceMetadataAsJsonP(ReconciliationService service, String callback, String baseServiceUrl)
    {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode obj = mapper.createObjectNode();
        obj.put("name", serviceName_);
        obj.put("schemaSpace", D2rUtils.URI_SPACE);
        obj.put("identifierSpace", D2rUtils.URI_SPACE);
                
        //view object
        ObjectNode viewObj = mapper.createObjectNode();
        viewObj.put("url", baseServiceUrl + "/view?id={{id}}");
        obj.put("view", viewObj);
        
        //preview object
        ObjectNode previewObj = mapper.createObjectNode();
        previewObj.put("url", baseServiceUrl + "/preview/template?id={{id}}");
        previewObj.put("width",430);
        previewObj.put("height",300);
        
        obj.put("preview", previewObj);
        
        //suggest
        //Global suggest object
        ObjectNode suggestObj = mapper.createObjectNode(); 
                        
        //type suggest (autocomplete)
        ObjectNode typeSuggestObj = mapper.createObjectNode();
        typeSuggestObj.put("service_url", baseServiceUrl);
        typeSuggestObj.put("service_path", "/suggest/type");
        typeSuggestObj.put("flyout_service_url", baseServiceUrl);
        typeSuggestObj.put("flyout_service_path" , "/suggest/type/preview");
                
        suggestObj.put("type", typeSuggestObj);
        
        //property suggest (autocomplete)
        ObjectNode propertySuggestObj = mapper.createObjectNode();
        propertySuggestObj.put("service_url", baseServiceUrl);
        propertySuggestObj.put("service_path", "/suggest/property");
        propertySuggestObj.put("flyout_service_url", baseServiceUrl);
        propertySuggestObj.put("flyout_service_path" , "/suggest/property/preview");
                
        suggestObj.put("property", propertySuggestObj);
        
        //entity search
        ObjectNode entitySearchObj = mapper.createObjectNode();
        entitySearchObj.put("service_url", baseServiceUrl);
        entitySearchObj.put("service_path", "/suggest/entity");
        entitySearchObj.put("flyout_service_url", baseServiceUrl);
        entitySearchObj.put("flyout_service_path" , "/suggest/entity/preview");
                
        suggestObj.put("entity", entitySearchObj);
        
        obj.put("suggest", suggestObj);
        
        return D2rUtils.toJSONP(callback, obj);
    }
}