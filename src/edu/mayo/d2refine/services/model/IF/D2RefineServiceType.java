package edu.mayo.d2refine.services.model.IF;

public enum D2RefineServiceType {
    
    TERM_RECONCILIATION {
        @Override
        public String toString() {
          return "term";
        }
      },
    CDE_RECONCILIATION {
        @Override
        public String toString() {
          return "cde";
        }
      },
    MODEL_RECONCILIATION {
        @Override
        public String toString() {
          return "model";
        }
      }
}
