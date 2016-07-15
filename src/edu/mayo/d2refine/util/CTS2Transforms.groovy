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
package edu.mayo.d2refine.util

import edu.mayo.d2refine.services.reconciliation.model.ReconciliationCandidate
import edu.mayo.d2refine.services.reconciliation.model.SearchResultItem
import org.apache.commons.lang.StringUtils
import org.json.JSONArray
import org.json.JSONObject
/**
 *  Transforms the results of CTS2 Rest API Calls
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
class CTS2Transforms {
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
                                
                        String[] types = [D2RC.ServiceType.TERM.toString()];
                        ReconciliationCandidate rc1 =
                                new ReconciliationCandidate(id: id, name: designation, types: types , score: score, match: Boolean.FALSE);
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
