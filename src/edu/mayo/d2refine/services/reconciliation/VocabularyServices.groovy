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
import edu.mayo.bsi.cts.cts2connector.cts2search.ConvenienceMethods
import edu.mayo.bsi.cts.cts2connector.cts2search.RESTContext
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.MatchAlgorithm
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchException
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.ServiceResultFormat
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.VocabularyId
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
class VocabularyServices {

    String cts2ServiceName
    MatchAlgorithm cts2MatchAlgorithm = MatchAlgorithm.EXACT
    ServiceResultFormat cts2ResultOutputFormat = ServiceResultFormat.JSON
    
    RESTContext cts2ServiceContext
    ConvenienceMethods cm_
    
    VocabularyServices(String propertyFilePath) {
        try {
            this.cm_ = ConvenienceMethods.instance(propertyFilePath)
        }
        catch(Exception e) {
            this.cm_ = ConvenienceMethods.instance()
        }
    }

    void prepare(String requestedCTS2ServiceName) throws SearchException {

        // If requested service profile does not exist then a default CTS2
        // Service profile will be returned.
        if ((!requestedCTS2ServiceName)||
            (!(requestedCTS2ServiceName in cm_.getAvailableProfiles())))
            cts2ServiceName = cm_.getDefaultProfileName()
        
        cts2ServiceContext = cm_.getContext(cts2ServiceName)
        cts2ServiceContext.outputFormat = cts2ResultOutputFormat
        cts2ServiceContext.matchAlgorithm_ = cts2MatchAlgorithm
    }
    
    String search (String cts2Service, VocabularyId vocabulary, String matchPhrase) {
        try {
            prepare(cts2Service);
            return cm_.getVocabularyEntities(matchPhrase, vocabulary, cts2ServiceContext)
        }
        catch(Exception e) {
            e.printStackTrace()
        }
    }
}
