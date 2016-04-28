package edu.mayo.d2refine.exporter.model;

import com.google.refine.model.Row;

public interface D2RefineTemplate 
{
    public String getName();
    public String getDescription();
    public String getPackageName();
    
    public String getConstraintName(Row row);
    public String getConstraintDescription(Row row);
    public DataType getConstraintDataType(Row row);    
    public Interval getInterval(Row row);
    public IntegerInterval getRowOccurrence(Row row);
    
    public String getValueSetName(Row row);
    public String getValueSetDescription(Row row);
    public String getValueSetMember(Row row);
    public String getValueSetMemberCode(Row row);
    public String getValue(Row row);
}
