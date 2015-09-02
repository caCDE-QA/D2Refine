package edu.mayo.d2refine.model;

import org.apache.commons.lang.StringUtils;


public class IntegerInterval extends Interval
{
    public int min;
    public int max;
    
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
            
            min = Integer.parseInt(maxVal);        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
    
