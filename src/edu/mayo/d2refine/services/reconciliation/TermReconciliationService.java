package edu.mayo.d2refine.services.reconciliation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    public ReconciliationCandidate reconcile(ReconciliationRequest request) 
    {
        List<ReconciliationCandidate> results = new ArrayList<ReconciliationCandidate>();
        
        String[] types = {};
        ReconciliationCandidate rc1 = new ReconciliationCandidate("100", "Chevy", types , 1.0, Boolean.FALSE);
        results.add(rc1);
        //ReconciliationCandidate rc2 = new ReconciliationCandidate("200", "C0001", null, 1.0, Boolean.FALSE);
        //results.add(rc2);
        /*
        for (int i=0; i < 10; i++)
        {
            String term = "TERM_" + RandomStringUtils.randomAlphanumeric(6).toUpperCase();
            ReconciliationCandidate rc = new ReconciliationCandidate("" + i, term, null, 1, Boolean.TRUE);
            results.add(rc);          
        }
        */
        
        return rc1;
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
