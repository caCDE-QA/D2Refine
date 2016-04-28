package edu.mayo.d2refine.reconciliation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.RefineServlet;
import com.google.refine.commands.Command;
import com.google.refine.commands.recon.ReconcileCommand;
import com.google.refine.model.AbstractOperation;
import com.google.refine.model.Project;

public class D2RefineReconciliationService extends Command 
{
    final static Logger logger = LoggerFactory.getLogger("D2Refine");
    protected void createOperation(Project project,
            HttpServletRequest request, JSONObject engineConfig) throws Exception 
    {
        logger.info("Here in D2RefineReconciliationService");
        //return super.createOperation(project, request, engineConfig);
    }
    
    @Override
    public void init(RefineServlet servlet) 
    {
        logger.info("in the init() of D2RefineReconciliationService Command");
        super.init(servlet);
    }
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        logger.info("Here in doPost() D2RefineReconciliationService");
        // TODO Auto-generated method stub
        super.doPost(request, response);
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("Here in doGet() D2RefineReconciliationService");
        // TODO Auto-generated method stub
        super.doGet(request, response);
    }
    
}
