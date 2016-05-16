package edu.mayo.d2refine.command;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.RefineServlet;
import com.google.refine.commands.Command;


public class D2RefineCommand extends Command 
{
    final static Logger logger = LoggerFactory.getLogger("D2RefineCommand");
    
    @Override
    public void init(RefineServlet servlet) 
    {
           logger.error("Initializing D2RefineCommand...");
           super.init(servlet);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
            try
            {        
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Type", "application/json");
    
//                Writer w = response.getWriter();
//                JSONWriter writer = new JSONWriter(w);
//                
//                writer.object();
//                writer.key("code"); writer.value("ok");
//                writer.key("service");
//                writer.endObject();
//                w.flush();
//                w.close();
            } 
            catch (Exception e) 
            {
                    respondException(response, e);
            }
    }
}
