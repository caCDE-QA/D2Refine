package edu.mayo.d2refine.reconciliation
import com.google.refine.RefineServlet
import com.google.refine.util.ParsingUtilities
import edu.mayo.d2refine.commands.AbstractReconciliationCommand
import edu.mayo.d2refine.services.model.IF.ServiceType
import edu.mayo.d2refine.services.recon.model.IF.ReconciliationService
import edu.mayo.d2refine.services.reconciliation.TermReconciliationService
import edu.mayo.d2refine.util.D2rUtils
import org.json.JSONArray
import org.json.JSONException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest

public class ReconciliationCommand extends AbstractReconciliationCommand
{
    final static Logger logger = LoggerFactory.getLogger("d2refine");

    @Override
    public void init(RefineServlet servlet) 
    {
           logger.info("Initializing ReconciliationCommand...")
           super.init(servlet)
    }
    
    @Override
    protected ReconciliationService getService(HttpServletRequest request)
            throws JSONException, IOException 
    {
        String id = D2rUtils.getIdForString(ServiceType.TERM);
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
