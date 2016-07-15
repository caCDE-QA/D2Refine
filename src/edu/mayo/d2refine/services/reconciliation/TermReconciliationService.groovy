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
    static FileProjectManager fm = ((FileProjectManager) FileProjectManager.singleton)
    static VocabularyServices service = null
    
    boolean refreshContext = false

    private boolean isServiceAvailable()
    {
        String path
        try {
            if ((!service)||refreshContext) {
                logger.warn("Refresing Service....")
                FileProjectManager fm = ((FileProjectManager) FileProjectManager.singleton)
                path = fm.getWorkspaceDir().getPath() + File.separator + D2RC.PROP_FILE_PATH
                service = new VocabularyServices(path)
            }
        }
        catch(Exception e) {
        }
        
        if (!service) {
            logger.warn("Could not read properties file from '" + path + "'")
            path = File.separator + "tmp" +File.separator + "CTS2Profiles.properties"
            logger.warn("trying to read properties file from '" + path + "'")
            service = new VocabularyServices(path)
        }       
        
        service != null
    }

    List<ReconciliationCandidate> reconcile(ReconciliationRequest reconciliationRequest) {

        Set<ReconciliationCandidate> candidates = new LinkedHashSet<ReconciliationCandidate>()

        if (isServiceAvailable()) {
            String entityDirectory = service.search(
                                    null,  null, reconciliationRequest.queryString)
            candidates = CTS2Transforms.readEntitiesAsCandidates(
                                        reconciliationRequest.queryString, entityDirectory)
        }

        new ArrayList<ReconciliationCandidate>(candidates)
    }
    
    List<SearchResultItem> suggestType(String searchTerm) {

        List<SearchResultItem> items = new ArrayList<SearchResultItem>();
        
        if (isServiceAvailable()) {
            String entityDirectory = service.search(null,  null, searchTerm)
            items = CTS2Transforms.readEntitiesAsResultItems(searchTerm, entityDirectory)
            
            items?.sort{a, b ->
                b.score <=> a.score
            }
        }
            
        items
    }

    @Override
    void save(FileOutputStream out) throws IOException {
        // TODO Auto-generated method stub        
    }

    void initialize(FileInputStream inputStream) {
        // TODO Auto-generated method stub       
    }
}
