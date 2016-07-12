package edu.mayo.d2refine.model.reconciliation
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import org.json.JSONException
import org.json.JSONWriter

interface ReconciliationService
{
        public ReconciliationResponse reconcile(ReconciliationRequest request)
        public ImmutableList<SearchResultItem> suggestType(String searchTerm)
        public ImmutableMap<String, ReconciliationResponse> reconcile(ImmutableMap<String, ReconciliationRequest> multiQueryRequest)
        
        public void save(FileOutputStream out) throws IOException
        public void writeAsJson(JSONWriter w)throws JSONException
        public void initialize(FileInputStream inputStream)
}
