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
package edu.mayo.d2refine.services.reconciliation.model

import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.JsonParseException
import org.codehaus.jackson.map.JsonMappingException
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.node.ArrayNode
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
public class ReconciliationRequest
{
        private static final int DEFAULT_LIMIT = 3;
        private static final int MAXIMUM_LIMIT = 10;
        
        private final String queryString;
        private int limit;
        private String[] types;
        private Properties context;
        
        public ReconciliationRequest(String query, int limit) 
        {
                this.queryString = query;
                this.limit = limit;
                this.types = [];
                this.context = new Properties();
        }
        
        public String getQueryString(){
                return queryString;
        }

        public int getLimit() {
                return limit;
        }

        public void setTypes(String[] types) 
        {
                this.types = types;
        }
        
        public String[] getTypes()
        {
                return types;
        }

        public Properties getContext() 
        {
                return context;
        }

        public void setContext(Properties context) 
        {
                this.context = context;
        }
        
        public void setLimit(int limit) 
        {
                this.limit = limit;
        }
        
        public static ReconciliationRequest valueOf(String json) throws JsonParseException, JsonMappingException, IOException
        {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readValue(json, JsonNode.class);
                //get query
                String query = node.path("query").getTextValue();
                //get types. can be a single string or an array of strings
                String[] types;
                
                JsonNode typesNode = node.path("type");
                if(! typesNode.isMissingNode()){
                        if(typesNode.isArray()){
                                ArrayNode typesNodeArray = (ArrayNode) typesNode;
                                types = new String[typesNodeArray.size()];
                                for(int i=0;i<typesNodeArray.size();i++){
                                        types[i] = typesNodeArray.get(i).getTextValue();
                                }
                        }else{
                                types = {typesNode.getTextValue()};
                        }
                }else{
                        types = {};
                }
                //get limit
                int limit = node.path("limit").isMissingNode()?getDefaultLimit(types.length):node.path("limit").getIntValue();
                
                //get context i.e. additional properties
                JsonNode propertiesNode = node.path("properties");
                Properties props = new Properties();
                if(propertiesNode.isArray()){
                        ArrayNode propertiesArray = (ArrayNode) propertiesNode;
                        for(int i=0; i<propertiesArray.size();i+=1){
                                JsonNode propertyNode = propertiesArray.get(0);
                                JsonNode pidNode = propertyNode.path("pid");
                                if(pidNode.isMissingNode()){
                                        //only support strongly-identified properties
                                        continue;
                                }
                                String pid = pidNode.getTextValue();
                                //get value
                                JsonNode valueNode = propertyNode.path("v");
                                JsonNode valueNodeId = valueNode.path("id");
                                String value;
                                if(valueNodeId.isMissingNode()){
                                        //textual value
                                        value = valueNode.getTextValue();
                                }else{
                                        //identified value
                                        value = valueNodeId.getTextValue();
                                }
                                props.setProperty(pid, value);
                        }
                }
                
                ReconciliationRequest request = new ReconciliationRequest(query, limit);
                request.setTypes(types);
                request.setContext(props);
                
                return request;
        }
        
        
        @Override
        public boolean equals(Object obj) 
        {
                if (obj==null) {return false;}
                if(!obj.getClass().equals(this.getClass())){ return false;}
                ReconciliationRequest otherReq = (ReconciliationRequest)obj;                
                return this.queryString.equals(otherReq.getQueryString()) && this.getLimit()==otherReq.limit && this.getTypes().equals(otherReq.getTypes());
        }

        /**
         * when types are restricted (i.e. typesLength>0) the query is specific and the default limit needs to be small thus we use DEFAULT_LIMIT
         * when types are not restricted (i.e. typesLength==0) the query is more of a an explorative nature and needs to see what types exist for what we are looking for
         * and thus we use the MAXIMUM_LIMIT 
         * @param typesLength
         * @return
         */
        private static int getDefaultLimit(int typesLength)
        {
                return typesLength==0?MAXIMUM_LIMIT:DEFAULT_LIMIT;
        }

}
