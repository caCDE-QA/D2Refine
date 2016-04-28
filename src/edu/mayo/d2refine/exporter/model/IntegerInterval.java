package edu.mayo.d2refine.exporter.model;

import org.apache.commons.lang.StringUtils;


public class IntegerInterval extends Interval
{
    public int min;
    public int max;
    
    public IntegerInterval(Integer minV, Integer maxV)
    {
        this.min = minV;
        this.max = maxV;
    }
    
    public IntegerInterval(String minVal, String maxVal)
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
            
            min = Integer.parseInt(minVal);        
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
            
            max = Integer.parseInt(maxVal);        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
    
