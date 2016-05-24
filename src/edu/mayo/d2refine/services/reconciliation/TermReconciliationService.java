package edu.mayo.d2refine.services.reconciliation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.ExtendedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import com.google.refine.ProjectManager;
import com.google.refine.RefineServlet;
import com.google.refine.io.FileProjectManager;

import edu.mayo.d2refine.model.ServiceType;
import edu.mayo.d2refine.model.reconciliation.ReconciliationCandidate;
import edu.mayo.d2refine.model.reconciliation.ReconciliationRequest;
import edu.mayo.d2refine.model.reconciliation.ReconciliationResponse;
import edu.mayo.d2refine.model.reconciliation.SearchResultItem;
import edu.mayo.d2refine.util.CTS2Transforms;
import edu.mit.simile.butterfly.ButterflyModule;

public class TermReconciliationService extends AbstractReconciliationService
{
    final static Logger logger = LoggerFactory.getLogger("TermReconciliationService");
    
    public static FileProjectManager fm = ((FileProjectManager) FileProjectManager.singleton);
    public static VocabularyServices service = null;
    
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
        
        FileProjectManager fm = ((FileProjectManager) FileProjectManager.singleton);
        String path = fm.getWorkspaceDir().getAbsolutePath();
        
        logger.warn("<<<<<<<<<<< PATH=" + fm.getWorkspaceDir().getAbsolutePath());
        
        if (service == null)
        {
            File extPath = fm.getWorkspaceDir();
            String propertiesPath = extPath.getAbsolutePath().replace(" ", "%20");
            propertiesPath += File.separator + "extensions" + File.separator + "D2Refine" + File.separator + "CTS2Profiles.properties";
            
            File propFile = null;
            try 
            {
                propFile = new File (new URI("file:///" + propertiesPath));
            } 
            catch (URISyntaxException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
                       
            service = new VocabularyServices("/Users/dks02/Library/Application\\ Support/OpenRefine/extensions/D2Refine/CTS2Profiles.properties");
        }
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
