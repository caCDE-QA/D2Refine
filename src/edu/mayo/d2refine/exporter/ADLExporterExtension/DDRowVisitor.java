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

import com.google.common.collect.ImmutableList;
import com.google.refine.browsing.RowVisitor;
import com.google.refine.model.Project;
import com.google.refine.model.Row;
import edu.mayo.d2refine.exporter.impl.DBGapConstants;
import edu.mayo.d2refine.exporter.model.*;
import edu.mayo.samepage.adl.impl.adl.ADLArchetype;
import edu.mayo.samepage.adl.impl.adl.ADLArchetypeHelper;
import edu.mayo.samepage.adl.impl.adl.ADLMetaData;
import edu.mayo.samepage.adl.impl.adl.env.IDType;
import edu.mayo.samepage.adl.impl.adl.rm.ADLRM;
import org.apache.commons.lang.StringUtils;
import org.openehr.adl.rm.RmType;
import org.openehr.adl.rm.RmTypeAttribute;
import org.openehr.jaxb.am.CAttribute;
import org.openehr.jaxb.am.CComplexObject;
import org.openehr.jaxb.am.CObject;
import org.openehr.jaxb.rm.MultiplicityInterval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.openehr.adl.am.AmObjectFactory.*;
import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfInteger;
import static org.openehr.adl.rm.RmObjectFactory.newIntervalOfReal;
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
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
    
   public ADLRM rmKind_ = null;
    
    public String topId = null;
        
    public DDRowVisitor(D2RefineTemplate template, String rmType, Writer writer) 
    {
        this.template_ = template;
        this.writer_ = writer;
        
        if ("OPENCIMI".equals(rmType))
            this.rmKind_ = ADLRM.OPENCIMI;
        else
            this.rmKind_ = ADLRM.OPENEHR;
        
        this.metadata_ = new ADLMetaData(this.rmKind_);
    }
    
    
    @Override
    public void start(Project project) 
    {
        //this.metadata_ = new ADLMetaData(ADLRM.OPENCIMI);
        this.metadata_.setDefaultTerminologySetName("snomed-ct");
        
        //this.archetypeRMClass_ = this.template_.getConstrainedRMClass(null);
                
        String archetypeName = this.template_.getName();
        String archetypeDescription = this.template_.getDescription();
        
        this.archetype_ = new ADLArchetype(archetypeName, metadata_);
        topId = this.archetype_.addNewId(IDType.TERM, archetypeName, archetypeDescription);       
    }

    @Override
    public boolean visit(Project project, int rowIndex, Row row) 
    {   
        if (this.archetype_ == null)
            logger.error("Archetype is null!... returning...");
        
        //logger.info("Row=" + rowIndex + ":" + row);
        
        DataType dt  = this.template_.getConstraintDataType(row);
        if (dt == null)
        {
            // If data type is null then check if this row has some
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
        
        String constraintName = this.template_.getConstraintName(row);
        String description = this.template_.getConstraintDescription(row);
        String constraintID = this.archetype_.addNewId(IDType.TERM, constraintName, description);   
        
        MultiplicityInterval multiplicity = null;
        
        IntegerInterval occurrence = this.template_.getRowOccurrence(row);
        if (occurrence != null)
            multiplicity = this.archetypeHelper_.createMultiplicity(occurrence.min, occurrence.max);
        else
            multiplicity = this.archetypeHelper_.createMultiplicity(0, 1);
        
                
        List<CObject> subConstraints = new ArrayList<CObject>();
        
        if (dt.isEncoded)
        {
            initValueSet(row);           
            processPermissibleValue(row);
            
            String constraint1ID = this.archetype_.addNewId(IDType.TERM, constraintName, description);
            
            RmType CODEDTEXT = metadata_.getRmType(getRMTypeName("CODED_TEXT"));
            
            CComplexObject constraint1 = null;
            
            if (this.rmKind_ != ADLRM.OPENEHR)
            {  
                List<CAttribute> consts = new ArrayList<CAttribute>();
                
                RmTypeAttribute terminologyId = metadata_.getRmAttribute(CODEDTEXT.getRmType(), "terminology_id");
                CObject vsConst = newCString(null, Arrays.asList(this.currentValueSetId_), null);                
                consts.add(this.archetypeHelper_.createAttributeConstraint(terminologyId, null, null, ImmutableList.<CObject>of(vsConst)));
                
                RmTypeAttribute code = metadata_.getRmAttribute(CODEDTEXT.getRmType(), "code");
                CObject cdConst = newCString(".*", null, null);
                consts.add(this.archetypeHelper_.createAttributeConstraint(code, null, null, ImmutableList.<CObject>of(cdConst)));
                                
                constraint1 = this.archetypeHelper_.createComplexObjectConstraint(CODEDTEXT, constraint1ID, null, consts);
            }
            else
            {
                RmTypeAttribute def_code = metadata_.getRmAttribute(CODEDTEXT.getRmType(), "defining_code");              
                CObject termCodeConst = newCTerminologyCode(this.currentValueSetId_, null);
                constraint1 = this.archetypeHelper_.createComplexObjectConstraint(CODEDTEXT, def_code, constraint1ID, null,  
                                                                            ImmutableList.<CObject>of(termCodeConst)); 
            }
            
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
        
        if ((interval == null)&&(!dt.isEncoded))
        {
            // It is either an identifier or some other simple/primitve constraints.
            String constrainedType = getIdentifyingType(constraintName, dt);
            RmType rmType = metadata_.getRmType(constrainedType);
            
            RmTypeAttribute rmAtt = null;
            String value = this.template_.getValue(row);
            List<CObject> restrictions = new ArrayList<CObject>();
            
            if (!StringUtils.isEmpty(value))
            {
                rmAtt = metadata_.getRmAttribute(rmType.getRmType(), "value"); 
                
                if (getRMTypeName("IDENTIFIER").equals(constrainedType))
                    rmAtt = metadata_.getRmAttribute(rmType.getRmType(), "id");
                
                String[] values = { value };
                restrictions.add(newCString(null, Arrays.asList(values), null));
            }
            
            String constraint3Id = this.archetype_.addNewId(IDType.TERM, constraintName, description);
            CComplexObject constraint3 = this.archetypeHelper_.createComplexObjectConstraint(rmType, rmAtt, constraint3Id, null, restrictions);
            
            if (constraint3 != null)
                subConstraints.add(constraint3);
        }
        
        String element = getRMTypeName("ELEMENT");
        RmType rmClass = metadata_.getRmType(element);
        RmTypeAttribute rmAttribute= metadata_.getRmAttribute(element, "value");        
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
            RmType COUNT = metadata_.getRmType(getRMTypeName("COUNT"));
            String countAttribute = (this.rmKind_ == ADLRM.OPENEHR)? "magnitude" : "value";
            RmTypeAttribute avalue = metadata_.getRmAttribute(COUNT.getRmType(), countAttribute);
            
            IntegerInterval ii = (IntegerInterval) interval;
            CObject intIntervalConst = newCInteger(newIntervalOfInteger(ii.min, ii.max), null);
            return this.archetypeHelper_.createComplexObjectConstraint(COUNT, avalue, id, occurrence01,  
                                                                        ImmutableList.<CObject>of(intIntervalConst)); 
        }
        
        if (interval instanceof RealInterval)
        {
 
            RmType QUANTITY = metadata_.getRmType(getRMTypeName("QUANTITY"));
            
            String quantityAttribute = (this.rmKind_ == ADLRM.OPENEHR)? "magnitude" : "value";
            RmTypeAttribute qvalue = metadata_.getRmAttribute(QUANTITY.getRmType(), quantityAttribute);
            
            RealInterval ri = (RealInterval) interval;
            CObject realIntervalConst = newCReal(newIntervalOfReal(ri.min, ri.max), null);
            return this.archetypeHelper_.createComplexObjectConstraint(QUANTITY, qvalue, id, occurrence01,  
                                                                    ImmutableList.<CObject>of(realIntervalConst)); 
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
        if ((this.valueSets_ != null) && (!this.valueSets_.isEmpty()))
            for (String vsId : this.valueSets_.keySet())
                if ((this.valueSets_.get(vsId) != null)&&(!this.valueSets_.get(vsId).isEmpty()))
                    this.archetype_.updateValueSet(vsId, this.valueSets_.get(vsId).toArray(new String[this.valueSets_.get(vsId).size()]));
        
        RmType ITEM_GROUP = metadata_.getRmType(getRMTypeName("ITEM_GROUP"));
        
        RmTypeAttribute item = null;
        
        if (this.rmKind_ == ADLRM.OPENEHR)
            item = metadata_.getRmAttribute(ITEM_GROUP.getRmType(), "items");
        else
            item = metadata_.getRmAttribute(ITEM_GROUP.getRmType(), "item");
        
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
    
    public String getIdentifyingType(String name, DataType type)
    {
        String lc = name.toLowerCase();
        
        if ((lc.indexOf(" id") != -1)||
            (lc.indexOf("_id") != -1)||
            (lc.endsWith("id")))
            return getRMTypeName("IDENTIFIER");
        
        if (DBGapConstants.RMATYPE_INTEGER.equals(type.type))
            return getRMTypeName("COUNT");
        
        if (DBGapConstants.RMATYPE_REAL.equals(type.type))
            return getRMTypeName("QUANTITY");
        
        return getRMTypeName("PLAIN_TEXT");        
    }
    
    private String getRMTypeName(String name)
    {
        if (this.rmKind_ == ADLRM.OPENEHR)
        {
            if ("ITEM_GROUP".equals(name))
                return "CLUSTER";
            
            if ("IDENTIFIER".equals(name))
                return "DV_IDENTIFIER";
            
            if ("COUNT".equals(name))
                return "DV_COUNT";
            
            if ("QUANTITY".equals(name))
                return "DV_QUANTITY";
            
            if ("PLAIN_TEXT".equals(name))
                return "DV_TEXT";
            
            if ("CODED_TEXT".equals(name))
                return "DV_CODED_TEXT";
        }
        
        return name;
    }
}
