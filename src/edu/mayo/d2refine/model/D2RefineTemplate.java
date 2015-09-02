package edu.mayo.d2refine.model;

import com.google.refine.model.Row;

import edu.mayo.d2refine.model.DataType;
import edu.mayo.d2refine.model.Interval;

public interface D2RefineTemplate 
{
    public String getName();
    public String getDescription();
    public String getPackageName();
    
    public String getConstraintName(Row row);
    public String getConstraintDescription(Row row);
    public DataType getConstraintDataType(Row row);    
    public Interval getInterval(Row row);
}
