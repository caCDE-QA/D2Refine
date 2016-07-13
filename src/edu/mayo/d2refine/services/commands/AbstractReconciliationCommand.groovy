package edu.mayo.d2refine.services.commands
import com.google.refine.RefineServlet
import com.google.refine.commands.Command
import edu.mayo.d2refine.services.reconciliation.ReconciliationService
import org.json.JSONException
import org.json.JSONWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

public abstract class AbstractReconciliationCommand extends Command
{
    final static Logger logger = LoggerFactory.getLogger("AbstractServiceCommand");
    
    @Override
    public void init(RefineServlet servlet) 
    {
           super.init(servlet);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
            try
            {
                ReconciliationService service = getService(request);
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Type", "application/json");
                    
                Writer w = response.getWriter();
                JSONWriter writer = new JSONWriter(w);
                
                writer.object();
                writer.key("code"); 
                writer.value("ok");
                writer.key("service");
                service.writeAsJson(writer);                
                writer.endObject();
                w.flush();
                w.close();
            } 
            catch (Exception e) 
            {
                    respondException(response, e);
            }
    }
    
    protected abstract ReconciliationService getService(HttpServletRequest request) throws JSONException, IOException;
}
