package edu.mayo.d2refine.commands.reconciliation;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import com.google.refine.RefineServlet;
import com.google.refine.util.ParsingUtilities;

import edu.mayo.d2refine.model.reconciliation.ReconciliationRequest;
import edu.mayo.d2refine.model.reconciliation.ReconciliationResponse;
import edu.mayo.d2refine.model.reconciliation.ReconciliationService;
import edu.mayo.d2refine.services.reconciliation.TermReconciliationService;
import edu.mayo.d2refine.util.D2rUtils;

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
