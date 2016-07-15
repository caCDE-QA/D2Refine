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
package edu.mayo.d2refine.services.reconciliation;

import org.apache.commons.lang.StringUtils;

import edu.mayo.bsi.cts.cts2connector.cts2search.ConvenienceMethods;
import edu.mayo.bsi.cts.cts2connector.cts2search.RESTContext;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.MatchAlgorithm;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchException;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.ServiceResultFormat;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.VocabularyId;
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
public class VocabularyServices 
{
    private String EMPTY = "";
    private String serviceName_ = null;
    private MatchAlgorithm matchAlgorithm_ = MatchAlgorithm.EXACT;
    private ServiceResultFormat outputFormat_ = ServiceResultFormat.JSON;
    
    private RESTContext serviceContext_ = null;
    
    private ConvenienceMethods cm_ = null;
    
    public VocabularyServices(String extensionProperties)
    {
        try
        {
            this.cm_ = ConvenienceMethods.instance(extensionProperties);
        }
        catch(Exception e)
        {
            this.cm_ = ConvenienceMethods.instance();
        }
    }
    
    public ServiceResultFormat getOutputFormat() 
    {
        return outputFormat_;
    }
   
    public void setOutputFormat(ServiceResultFormat outputFormat) 
    {
        this.outputFormat_ = outputFormat;
    }

    public String getServiceName() 
    {
        return serviceName_;
    }
    
    public void setServiceName(String serviceName) 
    {
        this.serviceName_ = serviceName;
    }

    public MatchAlgorithm getMatchAlgorithm() 
    {
        return matchAlgorithm_;
    }

    public void setMatchAlgorithm(MatchAlgorithm matchAlgorithm) 
    {
        this.matchAlgorithm_ = matchAlgorithm;
    }
    
    public void prepare(String serviceName) throws SearchException
    {
        if ((StringUtils.isBlank(serviceName))||
            (!cm_.getAvailableProfiles().contains(serviceName)))
            this.setServiceName(cm_.getDefaultProfileName());
        
        serviceContext_ = cm_.getContext(getServiceName());
        serviceContext_.setOutputFormat(getOutputFormat());
        serviceContext_.matchAlgorithm_ = getMatchAlgorithm();
    }
    
    public String search (String serviceName, VocabularyId vocabulary, String matchPhrase)
    {
        try
        {        
            prepare(serviceName);                        
            return cm_.getVocabularyEntities(matchPhrase, vocabulary, serviceContext_);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return EMPTY;
    }
}
