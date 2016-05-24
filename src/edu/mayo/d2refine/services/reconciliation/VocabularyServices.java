package edu.mayo.d2refine.services.reconciliation;

import org.apache.commons.lang.StringUtils;

import edu.mayo.bsi.cts.cts2connector.cts2search.ConvenienceMethods;
import edu.mayo.bsi.cts.cts2connector.cts2search.RESTContext;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.MatchAlgorithm;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.SearchException;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.ServiceResultFormat;
import edu.mayo.bsi.cts.cts2connector.cts2search.aux.VocabularyId;

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
