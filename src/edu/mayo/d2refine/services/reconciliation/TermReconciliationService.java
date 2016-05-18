package edu.mayo.d2refine.services.reconciliation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mayo.d2refine.model.ServiceType;
import edu.mayo.d2refine.model.reconciliation.ReconciliationCandidate;
import edu.mayo.d2refine.model.reconciliation.ReconciliationRequest;

public class TermReconciliationService extends AbstractReconciliationService
{
    final static Logger logger = LoggerFactory.getLogger("TermReconciliationService");
    
    public TermReconciliationService(String id, String name)
    {
        super(id, name);
        setServiceType(ServiceType.TERM_RECONCILIATION);
    }

    @Override
    public List<ReconciliationCandidate> reconcile(ReconciliationRequest request) 
    {
        List<ReconciliationCandidate> results = new ArrayList<ReconciliationCandidate>();
        for (int i=0; i < 10; i++)
        {
            String term = "TERM_" + RandomStringUtils.randomAlphanumeric(6).toUpperCase();
            ReconciliationCandidate rc = new ReconciliationCandidate("" + i, term, null, 1, Boolean.TRUE);
            results.add(rc);          
        }
        
        return results;
    }

    @Override
    public void save(FileOutputStream out)
            throws IOException 
    {
        // TODO Auto-generated method stub        
    }

    public void initialize(FileInputStream in) 
    {
        // TODO Auto-generated method stub       
    }
}
