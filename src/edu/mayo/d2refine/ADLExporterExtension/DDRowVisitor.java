package edu.mayo.d2refine.ADLExporterExtension;

import static org.openehr.adl.am.AmObjectFactory.newCAttribute;
import static org.openehr.adl.am.AmObjectFactory.newCComplexObject;
import static org.openehr.adl.am.AmObjectFactory.newCInteger;
import static org.openehr.adl.am.AmObjectFactory.newCReal;
import static org.openehr.adl.am.AmObjectFactory.newCString;
import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfInteger;
import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfReal;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openehr.adl.rm.RmType;
import org.openehr.adl.rm.RmTypeAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.am.CObject;
import org.openehr.jaxb.rm.MultiplicityInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import com.google.refine.browsing.RowVisitor;
import com.google.refine.model.Project;
import com.google.refine.model.Row;     

import edu.mayo.d2refine.model.D2RefineTemplate;
import edu.mayo.d2refine.model.DataType;
import edu.mayo.d2refine.model.IntegerInterval;
import edu.mayo.d2refine.model.Interval;
import edu.mayo.d2refine.model.RealInterval;
import edu.mayo.samepage.adl.impl.adl.ADLArchetype;
import edu.mayo.samepage.adl.impl.adl.ADLArchetypeHelper;
import edu.mayo.samepage.adl.impl.adl.ADLMetaData;
import edu.mayo.samepage.adl.impl.adl.env.IDType;
import edu.mayo.samepage.adl.impl.adl.rm.ADLRM;

public class DDRowVisitor implements RowVisitor 
{
    final static Logger logger = LoggerFactory.getLogger("DDRowVisitor");
    
    public ADLMetaData metadata_ = null;
    public ADLArchetype archetype_ = null;
    public ADLArchetypeHelper archetypeHelper_ = new ADLArchetypeHelper();;
    
    public D2RefineTemplate template_ = null;
    public Writer writer_ = null;
    
    public String adlText = "";
    
    public List<CObject> constraints_ = new ArrayList<CObject>();
    
    public HashMap<String, List<String>> valueSets_ = new HashMap<String, List<String>>();
    
    public String currentValueSetId_ = null;
    public List<String> currentValueSet_ = null;
    
    public String archetypeRMClass_ = null;
    
    public String topId = null;
        
    public DDRowVisitor(D2RefineTemplate template, Writer writer) 
    {
        this.template_ = template;
        this.writer_ = writer;
    }
    
    
    @Override
    public void start(Project project) 
    {
        try
        {
            System.out.println("STarting...");
            this.metadata_ = new ADLMetaData(ADLRM.OPENCIMI);
            
            System.out.println("after metadata call" + this.metadata_);
            
            this.archetypeRMClass_ = this.template_.getConstrainedRMClass(null);
                    
            String archetypeName = this.template_.getName();
            String archetypeDescription = this.template_.getDescription();
            
            this.archetype_ = new ADLArchetype(archetypeName, metadata_);
            topId = this.archetype_.addNewId(IDType.TERM, archetypeName, archetypeDescription);
        }
        catch (Exception e)
        {
            System.out.println("HERE############");
            e.printStackTrace();
        }
    }

    @Override
    public boolean visit(Project project, int rowIndex, Row row) 
    {   
        if (this.archetype_ == null)
            logger.error("Archetype is null!... returning...");
        
        logger.info("Row=" + rowIndex + ":" + row);
        
        DataType dt  = this.template_.getConstraintDataType(row);
        if (dt == null)
        {
            // If datatype is null then check if this row has some
            // permissive values to be included as members of already
            // defined value-set
            
            // Is there a live value-set available
            if(StringUtils.isEmpty(this.currentValueSetId_))
                return false;
            
            processPermissibleValue(row);
            
            return false;
        }
        
        // If this is a new row with data types it is time to add current value-set to list of value-sets.
        //
        if (!StringUtils.isEmpty(this.currentValueSetId_))
        {                
            this.valueSets_.put(this.currentValueSetId_, this.currentValueSet_);
                
            this.currentValueSetId_ = null;
            this.currentValueSet_ = null;
        }
        
        String constraintRMClassName = this.template_.getConstrainedRMClass(row);
        String attributeName = this.template_.getConstrainedRMClassAttribute(row, constraintRMClassName);
        
        String constraintName = this.template_.getConstraintName(row);
        String description = this.template_.getConstraintDescription(row);
        String constraintID = this.archetype_.addNewId(IDType.TERM, constraintName, description);   
        
        MultiplicityInterval multiplicity = null;
        
        IntegerInterval occurrence = this.template_.getRowOccurrence(row);
        if (occurrence != null)
            multiplicity = this.archetypeHelper_.createMultiplicity(occurrence.min, occurrence.max);
        
        RmType rmClass = metadata_.getRmType(constraintRMClassName);
        RmTypeAttribute rmAttribute= metadata_.getRmAttribute(constraintRMClassName, attributeName);
                
        List<CObject> subConstraints = new ArrayList<CObject>();
        
        if (dt.isEncoded)
        {
            initValueSet(row);           
            processPermissibleValue(row);
            
            String constraint1ID = this.archetype_.addNewId(IDType.TERM, constraintName, description);
            
            RmType CODEDTEXT = metadata_.getRmType("CODED_TEXT");
            RmTypeAttribute code = metadata_.getRmAttribute(CODEDTEXT.getRmType(), "code");
            RmTypeAttribute terminologyId = metadata_.getRmAttribute(CODEDTEXT.getRmType(), "terminology_id");

            CComplexObject constraint1 = newCComplexObject(CODEDTEXT.getRmType(), null, constraint1ID, ImmutableList.of(
                    newCAttribute(terminologyId.getAttributeName(), null, null, ImmutableList.<CObject>of(
                            newCString(null, Arrays.asList(this.currentValueSetId_), null)
                    )),
                    newCAttribute(code.getAttributeName(), null, null, ImmutableList.<CObject>of(
                            newCString(".*", null, null)
                    ))
            ));
            
            if (constraint1 != null)
                subConstraints.add(constraint1);
        }            
        
        Interval interval = this.template_.getInterval(row);        
        if (interval != null)
        {
                String constraint2Id = this.archetype_.addNewId(IDType.TERM, constraintName, description);
                CObject constraint2 = getCIMIInterval(interval, constraint2Id);
                
                if (constraint2 != null)
                    subConstraints.add(constraint2);
        }
        
        CComplexObject constraint = this.archetypeHelper_.createComplexObjectConstraint(rmClass, rmAttribute, constraintID, multiplicity, subConstraints);        
        constraints_.add(constraint);
                
        return false;
    }
    
    public CObject getCIMIInterval(Interval interval, String id)
    {
        if (interval == null)
            return null;
        
        MultiplicityInterval occurrence01 = this.archetypeHelper_.createMultiplicity(0, 1);
        if (interval instanceof IntegerInterval)
        {
            RmType COUNT = metadata_.getRmType("COUNT");
            RmTypeAttribute value = metadata_.getRmAttribute(COUNT.getRmType(), "value");
            
            IntegerInterval ii = (IntegerInterval) interval;
            
            return newCComplexObject(COUNT.getRmType(), occurrence01, id, ImmutableList.of(
                    newCAttribute(value.getAttributeName(), null, null, ImmutableList.<CObject>of(
                            newCInteger(newIntervalOfInteger(ii.min, ii.max), null)
                    ))
            ));
        }
        
        if (interval instanceof RealInterval)
        {
            RealInterval ri = (RealInterval) interval;
            
            RmType QUANTITY = metadata_.getRmType("QUANTITY");
            RmTypeAttribute value = metadata_.getRmAttribute(QUANTITY.getRmType(), "value");
            
            Double noval = new Double(0);
            
            return newCComplexObject(QUANTITY.getRmType(), occurrence01, id, ImmutableList.of(
                    newCAttribute(value.getAttributeName(), null, null, ImmutableList.<CObject>of(
                            newCReal(newIntervalOfReal(ri.min, ri.max), null)
                    ))
            ));
        }
        
        return null;
     }
    
    public void initValueSet(Row row)
    {       
        String valueSetName = this.template_.getValueSetName(row);
        String valueSetDescription = this.template_.getValueSetDescription(row);
        
        this.currentValueSetId_ = this.archetype_.addNewId(IDType.VALUESET, valueSetName, valueSetDescription);
        this.currentValueSet_ = new ArrayList<String>();
    }
    
    public void processPermissibleValue(Row row)
    { 
        String name = this.template_.getValueSetMember(row);
        String code = this.template_.getValueSetMemberCode(row);
                
        if (StringUtils.isEmpty(code))
            code = name;
        
        if (StringUtils.isEmpty(code))
            return;
        
        if (StringUtils.isEmpty(name))
            name = code;
        
        String pvId = this.archetype_.addNewId(IDType.VALUESETMEMBER, code, name);        
        this.archetype_.updateValueSet(this.currentValueSetId_, pvId);
    }
    
    
    @Override
    public void end(Project project) 
    {
        String groupingAttribute = this.template_.getConstrainedRMClassAttribute(null, archetypeRMClass_);
        
        if ((this.valueSets_ != null) && (!this.valueSets_.isEmpty()))
            for (String vsId : this.valueSets_.keySet())
                if ((this.valueSets_.get(vsId) != null)&&(!this.valueSets_.get(vsId).isEmpty()))
                    this.archetype_.updateValueSet(vsId, this.valueSets_.get(vsId).toArray(new String[this.valueSets_.get(vsId).size()]));
        
        RmType ITEM_GROUP = metadata_.getRmType("ITEM_GROUP");
        RmTypeAttribute item = metadata_.getRmAttribute(ITEM_GROUP.getRmType(), "item");
        MultiplicityInterval occurrence01 = this.archetypeHelper_.createMultiplicity(0, 1);
        
       CComplexObject topConstraint = this.archetypeHelper_.createComplexObjectConstraint(ITEM_GROUP, item, topId, occurrence01, this.constraints_);
       
       this.archetype_.setDefinition(topConstraint);
       
       String archText = this.archetype_.serialize();
        
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
