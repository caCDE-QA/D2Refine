package edu.mayo.d2refine.exporter.model;

import org.apache.commons.lang.StringUtils;


public class RealInterval extends Interval
{
    public double min;
    public double max;
    
    public RealInterval(double minVal, double maxVal)
    {
        this.min = minVal;
        this.max = maxVal;
    }
    
    public RealInterval(String minVal, String maxVal)
    {
        setMin(minVal);
        setMax(maxVal);
    }
    
    public void setMin(String minVal)
    {
        try
        {
            if (StringUtils.isEmpty(minVal))
                return;
            
            min = Float.parseFloat(minVal);        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void setMax(String maxVal)
    {
        try
        {
            if (StringUtils.isEmpty(maxVal))
                return;
            
            max = Float.parseFloat(maxVal);        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
    
