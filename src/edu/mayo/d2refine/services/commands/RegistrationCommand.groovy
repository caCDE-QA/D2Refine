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
import com.google.refine.RefineServlet
import com.google.refine.commands.Command
import org.json.JSONWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
class RegistrationCommand extends Command {

    final static Logger logger = LoggerFactory.getLogger("RegistrationCommand");
    
    @Override
    void init(RefineServlet servlet) {
        super.init(servlet);
    }

    @Override
    void doPost(HttpServletRequest request, HttpServletResponse response){
        try {
            def services = request.getParameter('services')

            response.setCharacterEncoding("UTF-8")
            response.setHeader("Content-Type", "application/json")

            Writer w = response.getWriter()
            JSONWriter writer = new JSONWriter(w)

            writer.object()
                writer.key("code"); writer.value("ok")
                writer.key("service");
                writer.object()
                    writer.key("id"); writer.value("d2refine")
                    writer.key("name"); writer.value("D2Refine")
                    writer.key("ui");
                    writer.object()
                        writer.key("handler"); writer.value("ReconStandardServicePanel")
                    writer.endObject()
                writer.endObject()
            writer.endObject()
            w.flush()
            w.close()
            }
            catch (Exception e) {
                respondException(response, e)
            }
    }
}
