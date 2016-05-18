package edu.mayo.d2refine.model.reconciliation;


import java.io.IOException;
import java.util.Properties;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

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
                this.types = new String[]{};
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
                                types = new String[] {typesNode.getTextValue()};
                        }
                }else{
                        types = new String[]{};
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
