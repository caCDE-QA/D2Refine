package edu.mayo.d2refine.services.reconciliation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mayo.d2refine.model.ServiceType;
import edu.mayo.d2refine.model.reconciliation.ReconciliationCandidate;
import edu.mayo.d2refine.model.reconciliation.ReconciliationRequest;
import edu.mayo.d2refine.model.reconciliation.ReconciliationResponse;

public class TermReconciliationService extends AbstractReconciliationService
{
    final static Logger logger = LoggerFactory.getLogger("TermReconciliationService");
    
    public TermReconciliationService(String id, String name)
    {
        super(id, name);
        setServiceType(ServiceType.TERM_RECONCILIATION);
    }

    public ReconciliationResponse reconcile(ReconciliationRequest request) 
    {
        Set<ReconciliationCandidate> candidates = new LinkedHashSet<ReconciliationCandidate>();
        //int limit = request.getLimit();
        
        double score = 0.0;
        if (request.getQueryString().toLowerCase().indexOf("chevy") != -1)
        {
            score = 1.0;
            
            String[] types = {};
            ReconciliationCandidate rc1 = new ReconciliationCandidate("100", "Chevy", types , score, Boolean.FALSE);
            candidates.add(rc1);
            ReconciliationCandidate rc2 = new ReconciliationCandidate("200", "C0001", types, score, Boolean.FALSE);
            candidates.add(rc2);
        }
        
        return wrapCandidates(new ArrayList<ReconciliationCandidate>(candidates));
    }
        
    private ReconciliationResponse wrapCandidates(List<? extends ReconciliationCandidate> candidates)
    {
        ReconciliationResponse response = new ReconciliationResponse();
        response.setResults(candidates);
        return response;
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
