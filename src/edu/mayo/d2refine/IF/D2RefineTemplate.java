package edu.mayo.d2refine.IF;


public interface D2RefineTemplate 
{
    public String getArchetypeName();    
    public String getConstraintName();
    public String getConstraintDescription();
    public DataType getConstraintDataType();    
    public Interval getInterval();
}
