package edu.mayo.d2refine.services.reconciliation.model

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
