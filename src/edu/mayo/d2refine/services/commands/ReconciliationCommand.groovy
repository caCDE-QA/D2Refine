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
package edu.mayo.d2refine.services.commands
import com.google.common.collect.ImmutableMap
import com.google.refine.RefineServlet
import edu.mayo.d2refine.services.reconciliation.ReconciliationService
import edu.mayo.d2refine.services.reconciliation.TermReconciliationService
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationRequest
import edu.mayo.d2refine.services.reconciliation.model.ReconciliationResponse
import edu.mayo.d2refine.util.D2rUtils
import org.json.JSONException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
public class ReconciliationCommand extends AbstractReconciliationCommand
{
    final static Logger logger = LoggerFactory.getLogger("d2refine");

    @Override
    public void init(RefineServlet servlet) 
    {
           logger.info("Initializing ReconciliationCommand...");
           super.init(servlet);
    }
    
    @Override
    protected ReconciliationService getService(HttpServletRequest request)
            throws JSONException, IOException 
    {
        String id = "terms";
        String name = "CTS2Reconciliation";
        return new TermReconciliationService(id, name, false);           
    } 
    
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
    {
            try
            {
                ReconciliationService service = getService(request);
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-Type", "application/json");                            
                String queries = request.getParameter("queries");
                ImmutableMap<String, ReconciliationRequest> multiQueryRequest = D2rUtils.getMultipleRequest(queries);
                ImmutableMap<String, ReconciliationResponse> multiResponse = service.reconcile(multiQueryRequest);
                response.getWriter().write(D2rUtils.getMultipleResponse(multiResponse).toString());
            } 
            catch (Exception e) 
            {
                    respondException(response, e);
            }
    }
}
