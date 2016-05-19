package edu.mayo.d2refine.model.reconciliation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONWriter;

import com.google.common.collect.ImmutableMap;

import edu.mayo.d2refine.model.Service;

public interface ReconciliationService extends Service
{
        public ReconciliationResponse reconcile(ReconciliationRequest request);        
        public ImmutableMap<String, ReconciliationResponse> reconcile(ImmutableMap<String, ReconciliationRequest> multiQueryRequest);
        
        public void save(FileOutputStream out) throws IOException;
        public void writeAsJson(JSONWriter w)throws JSONException;
        public void initialize(FileInputStream in);
}
