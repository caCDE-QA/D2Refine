package edu.mayo.d2refine.ADLExporterExtension;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.am.CPrimitiveObject;
import org.openehr.jaxb.am.CTerminologyCode;
import org.openehr.jaxb.rm.IntervalOfInteger;
import org.openehr.jaxb.rm.IntervalOfReal;
import org.openehr.jaxb.rm.MultiplicityInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.refine.browsing.RowVisitor;
import com.google.refine.model.Project;
import com.google.refine.model.Row;

import edu.mayo.d2refine.impl.DBGapConstants;
import edu.mayo.d2refine.impl.DBGapTemplate;
import edu.mayo.d2refine.model.D2RefineTemplate;
import edu.mayo.d2refine.model.DataType;
import edu.mayo.d2refine.model.IntegerInterval;
import edu.mayo.d2refine.model.Interval;
import edu.mayo.d2refine.model.RealInterval;
import edu.mayo.d2refine.model.StringInterval;
import edu.mayo.samepage.adl.IF.ADLServices;
import edu.mayo.samepage.adl.impl.adl.ADLArchetype;
import edu.mayo.samepage.adl.impl.adl.ADLArchetypeHelper;
import edu.mayo.samepage.adl.services.ADL2ServicesImpl;
import edu.mayo.samepage.adl.services.ADLTerminologyServices;
import edu.mayo.samepage.adl.services.CIMIPrimitiveTypes;
import edu.mayo.samepage.adl.services.CIMIRMMetaData;
import edu.mayo.samepage.adl.services.CIMITypes;

public class DDRowVisitor implements RowVisitor 
{
    final static Logger logger = LoggerFactory.getLogger("DDRowVisitor");
    
    private CIMIRMMetaData metadata_ = null;
    private ADLArchetype archetype_ = null;
    private ADLArchetypeHelper archetypeHelper_ = null;
    private ADLServices  adlServices_ = null;
    private D2RefineTemplate template_ = null;
    private Writer writer_ = null;
    
    private String adlText = "";
    
    private List<CComplexObject> constraints_ = new ArrayList<CComplexObject>();
    
    private HashMap<String, List<String>> valueSets_ = new HashMap<String, List<String>>();
    
    private String currentValueSetId_ = null;
    private List<String> currentValueSet_ = null;
    
    private String archetypeRMClass_ = null;
        
    public DDRowVisitor(D2RefineTemplate template, Writer writer) 
    {
        this.template_ = template;
        this.writer_ = writer;
    }
    
    
    @Override
    public void start(Project project) 
    {
        metadata_ = new CIMIRMMetaData();
        metadata_.setRMPackage(template_.getPackageName());
        
        archetypeRMClass_ = this.template_.getConstrainedRMClass(null);
        metadata_.setRMClassName(archetypeRMClass_);
        
        archetypeHelper_ = new ADLArchetypeHelper();
        adlServices_ = new ADL2ServicesImpl();
        
        String archetypeName = this.template_.getName();
        String archetypeDescription = this.template_.getDescription();
        
        this.archetype_ = adlServices_.createArchetype(archetypeName, archetypeDescription, metadata_, archetypeHelper_);
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
        
        CComplexObject constraint = processConstraint(row);
        
        if (constraint == null)
            return false;

        String constrainedAttribute = this.template_.getConstrainedRMClassAttribute(row, constraint.getRmTypeName());
        
        if (dt.isEncoded)
        {
            initValueSet(row);           
            processPermissibleValue(row);            
            CTerminologyCode terminologyCode = CIMITypes.getTerminologyConstraint(this.currentValueSetId_);
            this.archetypeHelper_.addAttributeConstraints(constraint, constrainedAttribute, null, null, terminologyCode);
        }
                                    
        Interval interval = this.template_.getInterval(row);        
        if (interval != null)
        {
            org.openehr.jaxb.rm.Interval amInterval = getCIMIInterval(interval);           
            CPrimitiveObject primitiveObject = CIMITypes.createPrimitiveTypeConstraints(CIMIPrimitiveTypes.INTEGER, amInterval, null);
            this.archetypeHelper_.addAttributeConstraints(constraint, constrainedAttribute, null, null, primitiveObject);
        }
            
        constraints_.add(constraint);
                
        return false;
    }
    
    private org.openehr.jaxb.rm.Interval getCIMIInterval(Interval interval)
    {
        if (interval == null)
            return null;
        
        if (interval instanceof IntegerInterval)
        {
            IntegerInterval ii = (IntegerInterval) interval;
            IntervalOfInteger amInterval = (IntervalOfInteger) CIMITypes.getIntervalFor(CIMIPrimitiveTypes.INTEGER);
            amInterval.setLower(ii.min);
            amInterval.setUpper(ii.max);
            return amInterval;
        }
        
        if (interval instanceof RealInterval)
        {
            RealInterval ri = (RealInterval) interval;
            IntervalOfReal amInterval = (IntervalOfReal) CIMITypes.getIntervalFor(CIMIPrimitiveTypes.REAL);
            amInterval.setLower(ri.min);
            amInterval.setUpper(ri.max);
            return amInterval;
        }
        
        return null;
     }
    
    private void initValueSet(Row row)
    {
        this.currentValueSetId_ = this.metadata_.createNewValueSetId();
        this.currentValueSet_ = new ArrayList<String>();
        
        String valueSetName = this.template_.getValueSetName(row);
        String valueSetDescription = this.template_.getValueSetDescription(row);
        String valueSetReference = ADLTerminologyServices.getConceptReference(this.currentValueSetId_);
        this.archetype_.addArchetypeTerm(this.currentValueSetId_, null, valueSetName, valueSetDescription, null, valueSetReference);
    }
    
    private void processPermissibleValue(Row row)
    {
        String id = this.metadata_.createNewPermissibleValueId();
        String name = this.template_.getValueSetMember(row);
        String code = this.template_.getValueSetMemberCode(row);
        
        if (StringUtils.isEmpty(code))
            code = name;
        
        if (StringUtils.isEmpty(code))
            return;
        
        if (StringUtils.isEmpty(name))
            name = code;
        
        String reference = ADLTerminologyServices.getConceptReference(id);
        this.archetype_.addArchetypeTerm(id, null, code, name, null, reference);
        
        if (!this.currentValueSet_.contains(id))
            this.currentValueSet_.add(id);
    }
    
    private CComplexObject processConstraint(Row row)
    {
        String constraintRMClass = this.template_.getConstrainedRMClass(row);
        String constraintID = this.metadata_.createNewId();
        
        String constraintName = this.template_.getConstraintName(row);
        String description = this.template_.getConstraintDescription(row);
        String conceptReference =  ADLTerminologyServices.getConceptReference(constraintID);   
        
        MultiplicityInterval multiplicity = null;
        
        IntegerInterval occurrence = this.template_.getRowOccurrence(row);
        if (occurrence != null)
            multiplicity = this.archetypeHelper_.createMultiplicityInterval(occurrence.min, occurrence.max);
        
        CComplexObject constraint = this.archetypeHelper_.createComplexObjectConstraint(constraintRMClass, constraintID, multiplicity);
        
        if (constraint != null)
            this.archetype_.addArchetypeTerm(constraintID, null, constraintName, description, null, conceptReference);
        
        return constraint;
    }
    
    @Override
    public void end(Project project) 
    {
        String groupingAttribute = this.template_.getConstrainedRMClassAttribute(null, archetypeRMClass_);
        
        if (!this.constraints_.isEmpty())
            this.archetypeHelper_.addAttributeConstraints(this.archetype_.getDefinition(), 
                                        groupingAttribute, null, null, 
                                        this.constraints_.toArray(new CComplexObject[this.constraints_.size()]));
        
        if ((this.valueSets_ != null) && (!this.valueSets_.isEmpty()))
            for (String vsId : this.valueSets_.keySet())
                if ((this.valueSets_.get(vsId) != null)&&(!this.valueSets_.get(vsId).isEmpty()))
                    this.archetype_.updateValueSet(vsId, this.valueSets_.get(vsId).toArray(new String[this.valueSets_.get(vsId).size()]));
        
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
