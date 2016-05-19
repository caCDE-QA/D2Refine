package edu.mayo.d2refine.model.reconciliation;

import java.util.ArrayList;
import java.util.List;

public class ReconciliationResponse 
{

        private List<? extends ReconciliationCandidate> results;
        
        public ReconciliationResponse()
        {
                this.results = new ArrayList<ReconciliationCandidate>();
        }
        
        public List<? extends ReconciliationCandidate> getResults() {
                return results;
        }

        public void setResults(List<? extends ReconciliationCandidate> results) {
                this.results = results;
        }
        
}
