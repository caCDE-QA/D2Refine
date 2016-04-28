package edu.mayo.d2refine.exporter.model;


public class StringInterval extends Interval
{
    public String min;
    public String max;
    
    public StringInterval(String minV, String maxV)
    {
        this.min = minV;
        this.max = maxV;
    }
}