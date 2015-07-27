package edu.mayo.d2refine.ADLExporterExtension;
import java.io.IOException;

import java.io.Writer;
import java.util.Properties;

import com.google.refine.browsing.Engine;
import com.google.refine.model.Project;
import com.google.refine.exporters.WriterExporter;

import edu.mayo.samepage.adl.reader.*;



public class ADLExporter implements WriterExporter
{
 
    public String getContentType(){
        return "application/x-unknown";
    }
    public void export(Project project, Properties options, Engine engine, Writer writer) throws IOException
    {
           ADL2Reader reader = new ADL2Reader();
           System.out.println("This is tested");
           return;
    }
}
