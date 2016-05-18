package edu.mayo.d2refine.model;

public enum ServiceType {    
    TERM_RECONCILIATION {
        @Override
        public String toString() {
          return "term";
        }
      },
    VALUESET_RECONCILIATION {
          @Override
          public String toString() {
            return "valueset";
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
