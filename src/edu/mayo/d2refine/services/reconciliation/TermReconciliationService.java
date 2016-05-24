package edu.mayo.d2refine.services.reconciliation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import edu.mayo.d2refine.model.ServiceType;
import edu.mayo.d2refine.model.reconciliation.ReconciliationCandidate;
import edu.mayo.d2refine.model.reconciliation.ReconciliationRequest;
import edu.mayo.d2refine.model.reconciliation.ReconciliationResponse;
import edu.mayo.d2refine.model.reconciliation.SearchResultItem;
import edu.mayo.d2refine.util.CTS2Transforms;

public class TermReconciliationService extends AbstractReconciliationService
{
    final static Logger logger = LoggerFactory.getLogger("TermReconciliationService");
    public static VocabularyServices service = new VocabularyServices("extensions/D2Refine/CTS2Profiles.properties");
    
    public TermReconciliationService(String id, String name)
    {
        super(id, name);
        setServiceType(ServiceType.TERM_RECONCILIATION);
    }

    public ReconciliationResponse reconcile(ReconciliationRequest request) 
    {
        Set<ReconciliationCandidate> candidates = new LinkedHashSet<ReconciliationCandidate>();
        //int limit = request.getLimit();
        
        String phrase = request.getQueryString();
        String entityDirectory = service.search(null,  null, phrase);
        candidates = CTS2Transforms.readEntitiesAsCandidates(phrase, entityDirectory);
        
        /*
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
        */
        
        return wrapCandidates(new ArrayList<ReconciliationCandidate>(candidates));
    }
    
    public ImmutableList<SearchResultItem> suggestType(String searchTerm)
    {
        List<SearchResultItem> items = new ArrayList<SearchResultItem>();
        
        String entityDirectory = service.search(null,  null, searchTerm);
        items = CTS2Transforms.readEntitiesAsResultItems(searchTerm, entityDirectory);
        
//        for (int i=0; i < 5; i++)
//        {
//                // Some random URI to show type
//                String id = searchTerm + "_optionId_" + (i+1);
//                String label = searchTerm + "_choice" + (i + 1);
//                double score = StringUtils.getLevenshteinDistance(label, searchTerm);
//                items.add(new SearchResultItem(id, label, score));
//        }
        
        Collections.sort(items, new Comparator<SearchResultItem>() {
                @Override
                public int compare(SearchResultItem o1, SearchResultItem o2) {
                        //descending order
                        return Double.compare(o2.getScore(), o1.getScore());
                }
        });
        
        return ImmutableList.copyOf(items);
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
