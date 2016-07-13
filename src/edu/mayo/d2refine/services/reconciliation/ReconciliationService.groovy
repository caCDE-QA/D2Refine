package edu.mayo.d2refine.services.reconciliation
import com.google.common.collect.ImmutableMap
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationCandidate
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationResponse
import org.json.JSONException
import org.json.JSONWriter

interface ReconciliationService
{
        public List<ReconciliationCandidate> reconcile(ReconciliationRequest request);
        public ImmutableMap<String, ReconciliationResponse> reconcile(ImmutableMap<String, ReconciliationRequest> multiQueryRequest);
        
        public void save(FileOutputStream out) throws IOException;
        public void writeAsJson(JSONWriter w)throws JSONException;
        public void initialize(FileInputStream inputStream);
}
