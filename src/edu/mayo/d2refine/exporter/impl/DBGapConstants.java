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
/**
 *
 *
 * @author <a href="mailto:sharma.deepak2@mayo.edu>Deepak Sharma</a>
 */
public class DBGapConstants 
{
    public static String DBGAP_PACKAGE_NAME = "DBGAP";
    
 // RM Specific
    public static String RMCLASS_ITEM_GROUP = "ITEM_GROUP";
    public static String RMCLASS_ELEMENT = "ELEMENT";
    public static String RMCLASS_COUNT = "COUNT";
    
    //RM Attributes used
    public static String RMATT_ITEM = "item";
    public static String RMATT_VALUE = "value";
    
    //RM Datatypes used
    public static String RMATYPE_INTEGER = "integer";
    public static String RMATYPE_REAL = "real";
    public static String RMATYPE_STRING = "string";
    
    public static String TEMPLATE_TYPE_ENCODED = "encode";
    public static String TEMPLATE_TYPE_STRING = "string";
    public static String TEMPLATE_TYPE_INTEGER = "integer";
    public static String TEMPLATE_TYPE_DECIMAL = "decimal";
    public static String TEMPLATE_TYPE_REAL = "real";
    public static String TEMPLATE_TYPE_FLOAT = "float";

    // Spreadsheet column names
    public static String VARIABLE_NAME = "variable - name";
    public static String VARIABLE_TYPE = "variable - type";
    public static String VARIABLE_DESC = "variable - description";
    public static String VARIABLE_MINVAL = "variable - logical_min";
    public static String VARIABLE_MAXVAL = "variable - logical_max";
    public static String VARIABLE_UNITS = "variable - unit";
    public static String VARIABLE_VALUE = "variable - value";
    public static String VARIABLE_CODED_VALUE_CODE = "variable - value - code";
    
    //Spreadsheet column indices
    public static int VARIABLE_NAME_INDEX = -1;
    public static int VARIABLE_TYPE_INDEX = -1;
    public static int VARIABLE_DESC_INDEX = -1;
    public static int VARIABLE_MINVAL_INDEX = -1;
    public static int VARIABLE_MAXVAL_INDEX = -1;
    public static int VARIABLE_UNITS_INDEX = -1;
    public static int VARIABLE_VALUE_INDEX = -1;
    public static int VARIABLE_CODED_VALUE_CODE_INDEX = -1;
}
