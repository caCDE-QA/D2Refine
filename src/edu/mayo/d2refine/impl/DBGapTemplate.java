package edu.mayo.d2refine.impl;

import org.apache.commons.lang.StringUtils;

import com.google.refine.model.Project;
import com.google.refine.model.Row;

import edu.mayo.d2refine.model.D2RefineTemplate;
import edu.mayo.d2refine.model.DataType;
import edu.mayo.d2refine.model.IntegerInterval;
import edu.mayo.d2refine.model.Interval;
import edu.mayo.d2refine.model.RealInterval;
import edu.mayo.d2refine.model.StringInterval;

public class DBGapTemplate implements D2RefineTemplate
{
    private Project project_;
    
    private String ENCODED_CHECK = "encode";
    
    public DBGapTemplate(Project project) 
    {
        this.project_ = project;
        
        if (DBGapConstants.VARIABLE_NAME_INDEX > -1)
            return;
        
        DBGapConstants.VARIABLE_NAME_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_NAME).getCellIndex();
        DBGapConstants.VARIABLE_TYPE_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_TYPE).getCellIndex();
        DBGapConstants.VARIABLE_DESC_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_DESC).getCellIndex();
        DBGapConstants.VARIABLE_MINVAL_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_MINVAL).getCellIndex();
        DBGapConstants.VARIABLE_MAXVAL_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_MAXVAL).getCellIndex();
        DBGapConstants.VARIABLE_UNITS_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_UNITS).getCellIndex();
        DBGapConstants.VARIABLE_CODED_VALUE_INDEX = project_.columnModel.getColumnByName(DBGapConstants.VARIABLE_CODED_VALUE).getCellIndex();
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
    public DataType getConstraintDataType(Row row) 
    {
        DataType dt = new DataType();
        dt.type = "string";
        dt.isEncoded = false;
        
        String type = getStringValue(row, DBGapConstants.VARIABLE_TYPE_INDEX);
        
        if (StringUtils.isEmpty(type))
            return dt;
        
        if (type.toLowerCase().contains(this.ENCODED_CHECK))
            dt.isEncoded = true;
        
        if (type.toLowerCase().contains("integer"))
            dt.type = "integer";
        
        if ((type.toLowerCase().contains("decimal"))||
                (type.toLowerCase().contains("float"))||
                (type.toLowerCase().contains("real")))
            dt.type = "real"; 
        
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
        
        if ("integer".equals(dt.type))
        {
            IntegerInterval ii = new IntegerInterval();
            ii.setMin(minVal);
            ii.setMax(maxVal);
            return ii;
        }
        
        if ("real".equals(dt.type))
        {
            RealInterval ri = new RealInterval();
            ri.setMin(minVal);
            ri.setMax(maxVal);
            return ri;
        }
        
        StringInterval si = new StringInterval();
        si.min = minVal;
        si.max = maxVal;
        return si;
    }

    @Override
    public String getPackageName() 
    {
        return "DBGAP";
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
    
