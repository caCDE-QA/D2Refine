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
import edu.mayo.d2refine.util.D2RC
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
class TermReconciliationService extends AbstractReconciliationService
{
    static FileProjectManager fm = ((FileProjectManager) FileProjectManager.singleton)
    VocabularyServices cts2Service = null
    
    boolean refreshContext = false

    private boolean isServiceAvailable()
    {
        String path
        try {
            if ((!cts2Service)||refreshContext) {
                logger.warn("Refresing Service....")
                FileProjectManager fm = ((FileProjectManager) FileProjectManager.singleton)
                path = fm.getWorkspaceDir().getPath() + File.separator + D2RC.PROP_FILE_PATH
                cts2Service = new VocabularyServices(path)
            }
        }
        catch(Exception e) {
        }
        
        if (!cts2Service) {
            logger.warn("Could not read properties file from '" + path + "'")
            path = File.separator + "tmp" +File.separator + "CTS2Profiles.properties"
            logger.warn("trying to read properties file from '" + path + "'")
            cts2Service = new VocabularyServices(path)
        }       
        
        cts2Service != null
    }

    List<ReconciliationCandidate> reconcile(ReconciliationRequest reconciliationRequest) {
        def candidates = new ArrayList<ReconciliationCandidate>()

        if (isServiceAvailable())
            return cts2Service.getReconciliationCandidates(reconciliationRequest.queryString)
        else
            logger.warn "Failed to get a Terminology Reconciliation Service"

        candidates
    }
    
    List<ReconciliationCandidate> suggestType(String searchTerm) {

        def candidates = new ArrayList<ReconciliationCandidate>()

        if (isServiceAvailable()) {
            candidates = cts2Service.getReconciliationCandidates(searchTerm)

            candidates?.sort { a, b ->
                b.score <=> a.score
            }
        }
        else
            logger.warn "Failed to get a Terminology Reconciliation Service"

        candidates
    }

    @Override
    void save(FileOutputStream out) throws IOException {
        // TODO Auto-generated method stub        
    }

    void initialize(FileInputStream inputStream) {
        // TODO Auto-generated method stub       
    }
}
