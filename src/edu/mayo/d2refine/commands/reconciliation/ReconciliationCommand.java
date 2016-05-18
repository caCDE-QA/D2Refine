package edu.mayo.d2refine.commands.reconciliation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.RefineServlet;

import edu.mayo.d2refine.model.ServiceType;
import edu.mayo.d2refine.model.reconciliation.ReconciliationService;
import edu.mayo.d2refine.services.reconciliation.TermReconciliationService;
import edu.mayo.d2refine.util.D2rUtils;

import com.google.refine.util.ParsingUtilities;

public class ReconciliationCommand extends AbstractReconciliationCommand 
{
    final static Logger logger = LoggerFactory.getLogger("d2refine");

    @Override
    public void init(RefineServlet servlet) 
    {
           logger.info("Initializing ReconciliationCommand...");
           super.init(servlet);
    }
    
    @Override
    protected ReconciliationService getService(HttpServletRequest request)
            throws JSONException, IOException 
    {
        String id = "terms";
        String name = "CTS2Reconciliation";
        
        ReconciliationService  service = new TermReconciliationService(id, name);
        
        try 
        {
            JSONArray arr = ParsingUtilities.evaluateJsonStringToArray(request.getParameter("services"));
            
            Set<String> urls = new HashSet<String>();
            for(int i=0;i<arr.length();i++)
            {
                urls.add(arr.getString(i));
            }
            
            // Here you store the service details to a file if needed - same as RDF plugin
            
            //GRefineServiceManager.singleton.synchronizeServices(urls);
            //GRefineServiceManager.singleton.addService(service);
            return service;
        } 
        catch (JSONException e) 
        {
            throw new RuntimeException("Failed to initialize Sindice service", e);
        }        
    }    
}
