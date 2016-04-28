package edu.mayo.d2refine.exporter.ADLExporterExtension;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.browsing.Engine;
import com.google.refine.browsing.FilteredRows;
import com.google.refine.exporters.WriterExporter;
import com.google.refine.model.Project;

import edu.mayo.d2refine.exporter.impl.DBGapTemplate;
import edu.mayo.d2refine.exporter.model.D2RefineTemplate;


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