package edu.mayo.d2refine.commands;

import com.google.refine.RefineServlet;
import com.google.refine.commands.Command;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class RegistrationCommand extends Command 
{
    final static Logger logger = LoggerFactory.getLogger("RegistrationCommand");
    
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
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Type", "application/json");
                    
                Writer w = response.getWriter();
                JSONWriter writer = new JSONWriter(w);

                writer.object();
                writer.key("code"); 
                writer.value("ok");
                writer.key("service");
                    writer.object();
                    writer.key("id"); 
                    writer.value("main");
                    writer.key("name"); 
                    writer.value("D2Refine");
                    writer.key("ui");
                        writer.object();
                        writer.key("handler");
                        writer.value("ReconStandardServicePanel");
                        writer.endObject();
                    writer.endObject();
                 writer.endObject();
                w.flush();
                w.close();
            } 
            catch (Exception e) 
            {
                    respondException(response, e);
            }
    }
}
