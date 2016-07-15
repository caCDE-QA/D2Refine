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
package edu.mayo.d2refine.exporter.ADLExporterExtension;

import com.google.refine.browsing.Engine;
import com.google.refine.browsing.FilteredRows;
import com.google.refine.exporters.WriterExporter;
import com.google.refine.model.Project;
import edu.mayo.d2refine.exporter.impl.DBGapTemplate;
import edu.mayo.d2refine.exporter.model.D2RefineTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
public class ADLExporter implements WriterExporter
{
    final static Logger logger = LoggerFactory.getLogger("ADLExporter");
    
    private String type_ = "OPENCIMI";
 
    public ADLExporter(String type)
    {
       this.type_ = type; 
    }
    
    @Override
    public String getContentType() 
    {
        return "text/plain";
    }
    
    public void export(Project project, Properties options, Engine engine, Writer writer) throws IOException
    { 
        logger.debug("Exporting Project " + project.getMetadata().getName() + " using " + this.type_ + " Reference Model...");
        D2RefineTemplate template = new DBGapTemplate(project);
        DDRowVisitor ddVisitor = new DDRowVisitor(template, this.type_, writer);
        
        FilteredRows allRows = engine.getAllRows();
        allRows.accept(project, ddVisitor);
        
        return;
    }
}
