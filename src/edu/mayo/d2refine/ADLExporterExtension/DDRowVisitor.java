package edu.mayo.d2refine.ADLExporterExtension;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.openehr.jaxb.am.CComplexObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.browsing.RowVisitor;
import com.google.refine.model.Project;
import com.google.refine.model.Row;

import edu.mayo.d2refine.model.D2RefineTemplate;
import edu.mayo.samepage.adl.IF.ADLServices;
import edu.mayo.samepage.adl.impl.adl.ADLArchetype;
import edu.mayo.samepage.adl.impl.adl.ADLArchetypeHelper;
import edu.mayo.samepage.adl.services.ADL2ServicesImpl;
import edu.mayo.samepage.adl.services.CIMIRMMetaData;

public class DDRowVisitor implements RowVisitor 
{
    final static Logger logger = LoggerFactory.getLogger("DDRowVisitor");
    
    private ADLArchetype archetype_ = null;
    ADLArchetypeHelper helper_ = null;
    private ADLServices  adlServices_ = null;
    private D2RefineTemplate template_ = null;
    private Writer writer_ = null;
    
    private String adlText = "";
    
    public DDRowVisitor(D2RefineTemplate template, Writer writer) 
    {
        this.template_ = template;
        this.writer_ = writer;
    }
    
    
    @Override
    public void start(Project project) 
    {
        CIMIRMMetaData cimi = new CIMIRMMetaData();
        cimi.setRMPackage(template_.getPackageName());        
        helper_ = new ADLArchetypeHelper();
        adlServices_ = new ADL2ServicesImpl();
        String archetypeName = this.template_.getName();
        String archetypeDescription = this.template_.getDescription();
        
        this.archetype_ = adlServices_.createArchetype(archetypeName, archetypeDescription, cimi, helper_);
    }

    @Override
    public boolean visit(Project project, int rowIndex, Row row) 
    {   
        if (this.archetype_ == null)
            logger.error("Archetype is null!... returning...");
        
        logger.info("Row=" + rowIndex + ":" + row);
        List<CComplexObject> constraints = new ArrayList<CComplexObject>();
        
        return false;
    }

    @Override
    public void end(Project project) 
    {
        String archText = adlServices_.serialize(archetype_);
        try 
        {
            this.writer_.write(archText);
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
