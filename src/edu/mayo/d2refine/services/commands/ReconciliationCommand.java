package edu.mayo.d2refine.services.commands;

import com.google.common.collect.ImmutableMap;
import com.google.refine.RefineServlet;
import edu.mayo.d2refine.services.reconciliation.ReconciliationService;
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest;
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationResponse;
import edu.mayo.d2refine.services.reconciliation.TermReconciliationService;
import edu.mayo.d2refine.util.D2rUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
        return new TermReconciliationService(id, name, false);           
    } 
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
            try
            {
                ReconciliationService service = getService(request);
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Type", "application/json");                            
                String queries = request.getParameter("queries");
                ImmutableMap<String, ReconciliationRequest> multiQueryRequest = D2rUtils.getMultipleRequest(queries);
                ImmutableMap<String, ReconciliationResponse> multiResponse = service.reconcile(multiQueryRequest);
                response.getWriter().write(D2rUtils.getMultipleResponse(multiResponse).toString());
            } 
            catch (Exception e) 
            {
                    respondException(response, e);
            }
    }
}
