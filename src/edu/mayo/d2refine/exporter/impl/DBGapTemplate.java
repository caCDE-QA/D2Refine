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
package edu.mayo.d2refine.exporter.impl;

import org.apache.commons.lang.StringUtils;

import com.google.refine.model.Project;
import com.google.refine.model.Row;

import edu.mayo.d2refine.exporter.model.D2RefineTemplate;
import edu.mayo.d2refine.exporter.model.DataType;
import edu.mayo.d2refine.exporter.model.IntegerInterval;
import edu.mayo.d2refine.exporter.model.Interval;
import edu.mayo.d2refine.exporter.model.RealInterval;
import edu.mayo.d2refine.exporter.model.StringInterval;
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
public class DBGapTemplate implements D2RefineTemplate
{
    private Project project_;
    
    public DBGapTemplate(Project project) 
    {
        this.project_ = project;
        
        //if (DBGapConstants.VARIABLE_NAME_INDEX > -1)
         //   return;
        
        DBGapConstants.VARIABLE_NAME_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_NAME).getCellIndex();
        DBGapConstants.VARIABLE_TYPE_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_TYPE).getCellIndex();
        DBGapConstants.VARIABLE_DESC_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_DESC).getCellIndex();
        DBGapConstants.VARIABLE_MINVAL_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_MINVAL).getCellIndex();
        DBGapConstants.VARIABLE_MAXVAL_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_MAXVAL).getCellIndex();
        DBGapConstants.VARIABLE_UNITS_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_UNITS).getCellIndex();
        DBGapConstants.VARIABLE_VALUE_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_VALUE).getCellIndex();
        DBGapConstants.VARIABLE_CODED_VALUE_CODE_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_CODED_VALUE_CODE).getCellIndex();
    }
    
    @Override
    public String getName() 
    {
        return project_.getMetadata().getName();
    }
    
    @Override
    public String getDescription() 
    {
        return project_.getMetadata().getName();
    }

    @Override
    public String getConstraintName(Row row) 
    {        
        return getStringValue(row, DBGapConstants.VARIABLE_NAME_INDEX);   
    }
    
    @Override
    public String getConstraintDescription(Row row) 
    {
        return getStringValue(row, DBGapConstants.VARIABLE_DESC_INDEX);
    }

    @Override
    public String getValueSetName(Row row)
    {
        return getConstraintName(row);  
    }
    
    @Override
    public String getValueSetDescription(Row row)
    {
        return getStringValue(row, DBGapConstants.VARIABLE_DESC_INDEX);  
    }
    
    @Override
    public String getValueSetMember(Row row) 
    {        
        return getValue(row);   
    }

    @Override
    public String getValue(Row row) 
    {        
        return getStringValue(row, DBGapConstants.VARIABLE_VALUE_INDEX);   
    }
    
    @Override
    public String getValueSetMemberCode(Row row) 
    {
        return getStringValue(row, DBGapConstants.VARIABLE_CODED_VALUE_CODE_INDEX);
    }
    
    @Override
    public DataType getConstraintDataType(Row row) 
    {
        String type = getStringValue(row, DBGapConstants.VARIABLE_TYPE_INDEX);
        
        if (StringUtils.isEmpty(type))
            return null;
        
        DataType dt = new DataType();
        dt.type = DBGapConstants.TEMPLATE_TYPE_STRING;
        dt.isEncoded = false;
              
        if (type.toLowerCase().contains(DBGapConstants.TEMPLATE_TYPE_ENCODED))
            dt.isEncoded = true;
        
        if (type.toLowerCase().contains(DBGapConstants.TEMPLATE_TYPE_INTEGER))
            dt.type = DBGapConstants.RMATYPE_INTEGER;
        
        if ((type.toLowerCase().contains(DBGapConstants.TEMPLATE_TYPE_DECIMAL))||
                (type.toLowerCase().contains(DBGapConstants.TEMPLATE_TYPE_FLOAT))||
                (type.toLowerCase().contains(DBGapConstants.TEMPLATE_TYPE_REAL)))
            dt.type = DBGapConstants.RMATYPE_REAL; 
        
        dt.units = getStringValue(row, DBGapConstants.VARIABLE_UNITS_INDEX);
        
        return dt;
    }

    @Override
    public Interval getInterval(Row row) 
    {
        String minVal = getStringValue(row, DBGapConstants.VARIABLE_MINVAL_INDEX);
        String maxVal = getStringValue(row, DBGapConstants.VARIABLE_MAXVAL_INDEX);
        
        if ((StringUtils.isEmpty(minVal))&&(StringUtils.isEmpty(maxVal)))
            return null;
        
        DataType dt = getConstraintDataType(row);
        
        if (DBGapConstants.RMATYPE_INTEGER.equals(dt.type))
            return new IntegerInterval(minVal, maxVal);
        
        if (DBGapConstants.RMATYPE_REAL.equals(dt.type))
            return  new RealInterval(minVal, maxVal);
        
        return new StringInterval(minVal, maxVal);
    }

    @Override
    public String getPackageName() 
    {
        return DBGapConstants.DBGAP_PACKAGE_NAME;
    }
    
    public IntegerInterval getRowOccurrence(Row row)
    {
        return new IntegerInterval(0, 1);
    }
        
    private String getStringValue(Row row, int index)
    {
        
        if ((row == null)||(index < 0))
            return null;
        
        Object obj = row.getCellValue(index);
        
        if (obj == null)
            return null;
        
        return obj.toString();
    }
}
    
