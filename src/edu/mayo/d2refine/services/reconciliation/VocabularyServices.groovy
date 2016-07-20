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
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationCandidate
import edu.mayo.d2refine.util.D2RC
import groovy.json.JsonSlurper
import org.apache.commons.lang.StringUtils

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
            e.printStackTrace()
        }
    }

    void prepare(String requestedCTS2ServiceName) throws SearchException {

        cts2ServiceName = requestedCTS2ServiceName
        // If requested service profile does not exist then a default CTS2
        // Service profile will be returned.
        if ((!requestedCTS2ServiceName)||
            (!(requestedCTS2ServiceName in cm_.getAvailableProfiles())))
            cts2ServiceName = cm_.getDefaultProfileName()
        
        cts2ServiceContext = cm_.getContext(cts2ServiceName)
        cts2ServiceContext.outputFormat = cts2ResultOutputFormat
        cts2ServiceContext.matchAlgorithm_ = cts2MatchAlgorithm
        cts2ServiceContext.resultLimit = 5
    }

    void addUpdateCTS2Context(String name, RESTContext context) {
        cm_.addUpdateRESTContext(name, context)
    }

    void addUpdateRESTBaseServiceURL(String name, String baseURL) {
        cm_.addUpdateRESTBaseServiceURL(name, baseURL)
    }

    String search (String cts2Service, VocabularyId vocabulary, String matchPhrase) {
        try {
            prepare(cts2Service);
            return cm_.getVocabularyEntities(matchPhrase, vocabulary, cts2ServiceContext)
        }
        catch(Exception e) {
            e.printStackTrace()
        }

        return null
    }

    List<ReconciliationCandidate> getReconciliationCandidates(String serviceId, String phrase){
        String entityDirectory = search(serviceId, null, phrase)
        parseReconciliationCandidates(phrase, entityDirectory)
    }

    List<ReconciliationCandidate> parseReconciliationCandidates(String phrase, String json) {
        List<ReconciliationCandidate> candidates = new ArrayList<ReconciliationCandidate>()

        try {
            if (json) {
                JsonSlurper slurper = new JsonSlurper()
                def entDir = slurper.parseText(json)
                int entries = entDir?.EntityDirectory?.numEntries as int

                if (entries) {
                    entDir.EntityDirectory?.entry?.each {
                        def termId = it.about
                        def ked = it.knownEntityDescription
                        def desc = (ked instanceof List) ? ked[0] : ked

                        def id = desc.href ?: termId

                        def ns = it.name?.namespace
                        String nameId = it.name?.name
                        def idQual = nameId ? ('[' + (ns ? "${ns}:" : '') + nameId + ']') : ''
                        double score = 0.0

                        def desgn = desc.designation

                        boolean isMatch = false;
                        if (phrase && desgn) {
                            score = StringUtils.getLevenshteinDistance(desgn, phrase)
                            isMatch = score == 0
                        }

                        desgn += idQual

                        String[] types = [D2RC.ServiceType.TERM.toString()];

                        ReconciliationCandidate rc =
                                new ReconciliationCandidate(id: id, name: desgn, types: types, score: score, match: isMatch);
                        candidates.add(rc)
                    }

                }
            }
        }
        catch (Exception e) {
            if (e.getMessage().indexOf("not found") == -1)
                e.printStackTrace();
        }

        candidates
    }
}
