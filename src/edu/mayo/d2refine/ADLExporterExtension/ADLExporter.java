package edu.mayo.d2refine.ADLExporterExtension;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.browsing.Engine;
import com.google.refine.model.Project;
import com.google.refine.exporters.WriterExporter;

import edu.mayo.samepage.utils.ADLUtils;

public class ADLExporter implements WriterExporter
{
    final static Logger logger = LoggerFactory.getLogger("ADLExporter");
 
    @Override
    public String getContentType() 
    {
        return "text/plain";
    }
    
    public void export(Project project, Properties options, Engine engine, Writer writer) throws IOException
    {
           String reader = ADLUtils.getTestArchetypeText();
           System.out.println("This is tested");
           writer.write(reader);
           return;
    }
}
