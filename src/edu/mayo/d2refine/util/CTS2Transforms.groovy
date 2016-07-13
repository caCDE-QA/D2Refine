package edu.mayo.d2refine.util
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationCandidate
import edu.mayo.d2refine.services.reconciliation.model.SearchResultItem
import org.apache.commons.lang.StringUtils
import org.json.JSONArray
import org.json.JSONObject

public class CTS2Transforms
{
    public static Set<ReconciliationCandidate> readEntitiesAsCandidates(String phrase, String json)
    {
        Set<ReconciliationCandidate> candidates = new LinkedHashSet<ReconciliationCandidate>();
        
        try
        {
            if (!StringUtils.isBlank(json))
            {
                JSONObject obj = new JSONObject(json);
                
                JSONObject entityDirectory = obj.getJSONObject("EntityDirectory");
                
                if ((entityDirectory != null)&&(Integer.valueOf(entityDirectory.getString("numEntries")) > 0))
                {
                    // TODO: Check if it not an array - check in case phrase "cigra" using py4cts2
                    JSONArray entries = entityDirectory.getJSONArray("entry");
                    for (int i = 0; i < entries.length(); i++)
                    {
                        JSONObject entry = entries.getJSONObject(i);
                        
                        if (entry == null)
                            continue;
                        
                        String termId = entry.getString("about");
                        
                        JSONObject entityDesc = null;
                        Object ked = entry.get("knownEntityDescription");
                        
                        if (ked instanceof JSONArray)
                        {
                            entityDesc = ((JSONArray) ked).getJSONObject(0);
                        }
                        else    
                            entityDesc = (JSONObject) ked; 
                        
                        String designation = entityDesc.getString("designation");
                        
                        String id = entityDesc.getString("href");
                        
                        if (StringUtils.isBlank(id))
                            id = termId;
                        
                        String idQualification = "";
                        try
                        {
                            String ns = entry.getJSONObject("name").getString("namespace");
                            String nameId = entry.getJSONObject("name").getString("name");
                            
                            if (!StringUtils.isBlank(ns))
                                idQualification = " [" + ns ;
                            
                            if (!StringUtils.isBlank(nameId))
                                if (StringUtils.isBlank(idQualification))
                                    idQualification = " [" + nameId + "]" ;
                                else
                                    idQualification += ":" + nameId + "]" ;
                        }
                        catch(Exception e)
                        {
                            // No need to throw exception if for some reason name-space information
                            // is not there. need to put better code here to check though.
                        }
                        
                        double score = 0.0;
                        
                        if ((!StringUtils.isBlank(phrase))&&
                                (!StringUtils.isBlank(designation)))   
                            score = StringUtils.getLevenshteinDistance(designation, phrase);
                        
                        designation += idQualification;
                                
                        String[] types = [ServiceType.TERM.toString()];
                        ReconciliationCandidate rc1 = new ReconciliationCandidate(id, designation, types , score, Boolean.FALSE);
                        candidates.add(rc1);                       
                    }
                }
            }
        }
        catch (Exception e)
        {
            if (e.getMessage().indexOf("not found") == -1)
                e.printStackTrace();
        }
        
        return candidates;
    }
    
    public static List<SearchResultItem> readEntitiesAsResultItems(String phrase, String json)
    {
        List<SearchResultItem> items = new ArrayList<SearchResultItem>();
        
        try
        {
            if (!StringUtils.isBlank(json))
            {
                JSONObject obj = new JSONObject(json);
                
                JSONObject entityDirectory = obj.getJSONObject("EntityDirectory");
                
                if ((entityDirectory != null)&&(Integer.valueOf(entityDirectory.getString("numEntries")) > 0))
                {
                    JSONArray entries = entityDirectory.getJSONArray("entry");
                    for (int i = 0; i < entries.length(); i++)
                    {
                        JSONObject entry = entries.getJSONObject(i);
                        
                        if (entry == null)
                            continue;
                        
                        String termId = entry.getString("about");
                        
                        JSONObject entityDesc = null;
                        
                        Object ked = entry.get("knownEntityDescription");
                        
                        if (ked instanceof JSONArray)
                        {
                            entityDesc = ((JSONArray) ked).getJSONObject(0);
                        }
                        else    
                            entityDesc = (JSONObject) ked; 
                        
                        String designation = entityDesc.getString("designation");
                        
                        String id = entityDesc.getString("href");
                        
                        if (StringUtils.isBlank(id))
                            id = termId;
                        
                        String idQualification = "";
                        try
                        {
                            String ns = entry.getJSONObject("name").getString("namespace");
                            String nameId = entry.getJSONObject("name").getString("name");
                            
                            if (!StringUtils.isBlank(ns))
                                idQualification = " [" + ns ;
                            
                            if (!StringUtils.isBlank(nameId))
                                if (StringUtils.isBlank(idQualification))
                                    idQualification = " [" + nameId + "]" ;
                                else
                                    idQualification += ":" + nameId + "]" ;
                        }
                        catch(Exception e)
                        {
                            // No need to throw exception if for some reson namespace information
                            // is not there. need to put better code here to check though.
                        }
                        
                        double score = 0.0;
                        
                        if ((!StringUtils.isBlank(phrase))&&
                                (!StringUtils.isBlank(designation)))   
                            score = StringUtils.getLevenshteinDistance(designation, phrase);
                        
                        designation += idQualification;
                        
                        items.add(new SearchResultItem(id, designation, score));              
                    }
                }
            }
        }
        catch (Exception e)
        {
            if (e.getMessage().indexOf("not found") == -1)
                e.printStackTrace();
        }
        
        return items;
    }
}
