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
package edu.mayo.d2refine.services.reconciliation.model;
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
public class ReconciliationCandidate {

        private final String id;
        private final String name;
        private String[] types;
        private final double score;
        private boolean match;
        
        public ReconciliationCandidate(String id, String name, String[] types, double score, boolean match){
                this.id = id;
                this.name = name;
                this.types = types;
                this.score = score;
                this.match = match;
        }

        public String getId() {
                return id;
        }

        public boolean isMatch() {
                return match;
        }

        public void setMatch(boolean match) {
                this.match = match;
        }

        public String getName() {
                return name;
        }

        public String[] getTypes() {
                return types;
        }

        public double getScore() {
                return score;
        }

        @Override
        public int hashCode() {
                return id.hashCode();
        }

        public void setTypes(String[] types) {
                this.types = types;
        }

        @Override
        public boolean equals(Object obj) {
                if(obj==null) return false;
                if(!obj.getClass().equals(this.getClass())) return false;
                return this.getId().equals(((ReconciliationCandidate)obj).getId());
        }        
}
