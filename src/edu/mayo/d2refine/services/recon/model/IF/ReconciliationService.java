package edu.mayo.d2refine.services.recon.model.IF;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONWriter;

import com.google.common.collect.ImmutableMap;

import edu.mayo.d2refine.services.model.IF.Service;
import edu.mayo.d2refine.services.recon.model.ReconciliationCandidate;
import edu.mayo.d2refine.services.recon.model.ReconciliationRequest;

public interface ReconciliationService extends Service
{
        public List<ReconciliationCandidate> reconcile(ReconciliationRequest request);        
        public ImmutableMap<String, ReconciliationCandidate> reconcile(ImmutableMap<String, ReconciliationRequest> multiQueryRequest);
        
        public void save(FileOutputStream out) throws IOException;
        public void writeAsJson(JSONWriter w)throws JSONException;
        public void initialize(FileInputStream in);
}
