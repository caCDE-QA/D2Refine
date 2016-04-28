package edu.mayo.d2refine.reconciliation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.RefineServlet;
import com.google.refine.commands.Command;

public class D2RefineInitialization extends Command 
{
    final static Logger logger = LoggerFactory.getLogger("D2Refine");
    
    @Override
    public void init(RefineServlet servlet) 
    {
        logger.info("in the init() of D2RefineInitialization Command");
        super.init(servlet);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException 
    {
        logger.info("in the doPost() of D2RefineInitialization Command");
            doGet(request,response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
                    throws ServletException, IOException 
    {
        logger.info("in the doGet() of D2RefineInitialization Command");
            throw new UnsupportedOperationException("This command is not meant to be called. It is just necessary for initialization");
    }
}
