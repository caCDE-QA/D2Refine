/*
 Copyright (c) 2016, Mayo Clinic
 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 Redistributions of source code must retain the above copyright notice, this
     list of conditions and the following disclaimer.

     Redistributions in binary form must reproduce the above copyright notice,
     this list of conditions and the following disclaimer in the documentation
     and/or other materials provided with the distribution.

     Neither the name of the <ORGANIZATION> nor the names of its contributors
     may be used to endorse or promote products derived from this software
     without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package edu.mayo.d2refine.services.reconciliation
import com.google.common.collect.ImmutableList
import com.google.refine.io.FileProjectManager
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationCandidate
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest
import edu.mayo.d2refine.services.reconciliation.model.SearchResultItem
import edu.mayo.d2refine.util.CTS2Transforms
import edu.mayo.d2refine.util.D2RC
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
class TermReconciliationService extends AbstractReconciliationService
{
    public static FileProjectManager fm = ((FileProjectManager) FileProjectManager.singleton)
    public static VocabularyServices service = null
    
    boolean refreshContextForEachRequest_ = false
    
    public TermReconciliationService(String id, String name, boolean refreshContextForEachRequest)
    {
        setId(id)
        setName(name)
        setServiceType(D2RC.ServiceType.TERM)
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
