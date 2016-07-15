package edu.mayo.d2refine.services
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import edu.mayo.d2refine.services.reconciliation.ReconciliationService
import edu.mayo.d2refine.services.reconciliation.TermReconciliationService
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationResponse
import edu.mayo.d2refine.services.reconciliation.model.SearchResultItem
import edu.mayo.d2refine.util.D2RC
import edu.mayo.d2refine.util.D2rUtils
import groovy.json.JsonBuilder

import javax.servlet.http.HttpServletRequest

public class D2RefineServiceManager
{
    static final String serviceName_ = "D2Refine"
    static D2RefineServiceManager manager_
    
    private D2RefineServiceManager() {}
    /**
     * Create a static method to get instance.
     */
    static D2RefineServiceManager instance()
    {
        if(!manager_)
            manager_ = new D2RefineServiceManager();
        
        manager_;
    }
     
    String handle(String path, HttpServletRequest request)
            throws IOException
    {
        // Callback will only be there when service meta data is requested
        String callback = request.getParameter('callback');
        
        String id = 'terms'
        String name = 'CTS2Reconciliation'
        ReconciliationService  service = new TermReconciliationService(id, name, false)
        String serviceURL = request.getRequestURL().toString()
        
        if (path.endsWith("main"))
        {
            if (!callback)
            {
                String queries = request.getParameter("queries")
                if (queries)
                {
                    ImmutableMap<String, ReconciliationRequest> multiQueryRequest = D2rUtils.getMultipleRequest(queries)
                    ImmutableMap<String, ReconciliationResponse> multiResponse = service.reconcile(multiQueryRequest)
                    return D2rUtils.getMultipleResponse(multiResponse)
                }
            }
        
            getServiceMetadataAsJsonP(service, callback, serviceURL)
        }
        else
        {
            String prefix = request.getParameter("prefix")
            ImmutableList<SearchResultItem> results = service.suggestType(prefix)
            D2rUtils.toJSONP(callback, D2rUtils.jsonizeSearchResult(results, prefix))
        }
    }
    
    public String getServiceMetadataAsJsonP(ReconciliationService service, String callback, String baseServiceUrl)
    {
        def jsonBuilder = new JsonBuilder()

        jsonBuilder{
            name serviceName_
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