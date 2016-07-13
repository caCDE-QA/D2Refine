package edu.mayo.d2refine.services.reconciliation
import com.google.common.collect.ImmutableList
import com.google.refine.io.FileProjectManager
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationCandidate
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest
import edu.mayo.d2refine.services.reconciliation.model.SearchResultItem
import edu.mayo.d2refine.util.CTS2Transforms
import edu.mayo.d2refine.util.ServiceType

class TermReconciliationService extends AbstractReconciliationService
{
    public static FileProjectManager fm = ((FileProjectManager) FileProjectManager.singleton)
    public static VocabularyServices service = null
    
    boolean refreshContextForEachRequest_ = false
    
    public TermReconciliationService(String id, String name, boolean refreshContextForEachRequest)
    {
        setId(id)
        setName(name)
        setServiceType(ServiceType.TERM)
        refreshContextForEachRequest_ = refreshContextForEachRequest;
    }

    private boolean isServiceAvailable()
    {
        String path = "";
        try
        {
            if ((!service)||refreshContextForEachRequest_)
            {
                logger.warn("Refresing Service....")
                FileProjectManager fm = ((FileProjectManager) FileProjectManager.singleton)
                path = fm.getWorkspaceDir().getPath() + 
                    File.separator + "extensions" + 
                    File.separator + "D2Refine" + 
                    File.separator + "CTS2Profiles.properties"
            
                service = new VocabularyServices(path)
            }
        }
        catch(Exception e)
        {
            
        }
        
        if (!service)
        {
            logger.warn("Could not read properties file from '" + path + "'")
            path = File.separator + "tmp" +File.separator + "CTS2Profiles.properties"
            logger.warn("trying to read properties file from '" + path + "'")
            service = new VocabularyServices(path)
        }       
        
        service != null
    }

    public List<ReconciliationCandidate> reconcile(ReconciliationRequest request)
    {
        Set<ReconciliationCandidate> candidates = new LinkedHashSet<ReconciliationCandidate>();
        //int limit = request.getLimit();
        
        String phrase = request.getQueryString();
        
        if (isServiceAvailable())
        {
            /*  TODO : fix the issue of opening the properties files from the workspace dir.
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
             */
        
            
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
        }

        new ArrayList<ReconciliationCandidate>(candidates)
    }
    
    public ImmutableList<SearchResultItem> suggestType(String searchTerm)
    {
        List<SearchResultItem> items = new ArrayList<SearchResultItem>();
        
        if (isServiceAvailable())
        {
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
        }
            
        return ImmutableList.copyOf(items);
    }
    


    @Override
    public void save(FileOutputStream out)
            throws IOException 
    {
        // TODO Auto-generated method stub        
    }

    public void initialize(FileInputStream inputStream)
    {
        // TODO Auto-generated method stub       
    }
}