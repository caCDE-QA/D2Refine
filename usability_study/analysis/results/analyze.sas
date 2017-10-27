/**************analyze data for Deepak Sharma **********/



     proc import datafile="~/consult/sharma/rawdata.xls" out=data dbms=xls replace; getnames=yes; run;
     
     proc format ;
          value s   1    =    ' D2R'
                    2    =    'OTM'
                    3    =    'RTF';
                    
                    
          value w   -2    =    'Strongly Disagree'
                    -1    =    'Disagree'
                    0   =    'Neither Agree or Disagree'
                    1    =    'Agree'
                    2    =    'Strongly Agree';
                    
          value yn    0    =    'No'
                    1    =    'Yes';

     run;
     data data2;
          set data;
           if fav = 1 then favorite = 1;
           else favorite = 0;
          
          label t1q1 = "Task 1: Create and viewing data dictionary - The system allows me to accomplish the task well?"
                t2q1 = "Task 2: Updating existing data dictionary - The system allows me to accomplish the task well?"
                t3q1 = "Task 3: Searching and binding controlled terminology terms - The system allows me to accomplish the task well?"
                
                t1q2 = "Task 1: Create and viewing data dictionary - The system enables me to accomplish the task well - according to my perceived understanding and expectation of the goal?"
                t2q2 = "Task 2: Updating existing data dictionary - The system enables me to accomplish the task well - according to my perceived understanding and expectation of the goal?"
                t3q2 = "Task 3: Searching and binding controlled terminology terms - The system enables me to accomplish the task well - according to my perceived understanding and expectation of the goal?"
                
                favorite = 'Favorite System?'
                
                t1time = "Time to complete task 1 (seconds)"
                t2time = "Time to complete task 2 (seconds)"
                t3time = "Time to complete task 3 (seconds)"
                
                score = 'Survey Sum' score_w="Survey Sum - Weighted"
                
                system = "System";
                
                  
          
          format t: w. system s. favorite yn. ;
          
     run;
     
  
     
      
     %table(dsn=data2, var=t1q1 t1q2 t1time t2q1 t2q2 t2time t3q1 t3q2 t3time favorite score score_w ,by=system, 
     type=2 2 1 2 2 1 2 2 1 2 1 1, labelwrap=y,ttitle1=System,
     pvalues= N, date=n,total=n,outdoc=~/consult/sharma/Descriptive.doc);
     
      
     %table(dsn=data2, var=t1q1 t1q2 t1time t2q1 t2q2 t2time t3q1 t3q2 t3time favorite score score_w ,by=system, 
     type=1 1 1 1 1 1 1 1 1 2 1 1, labelwrap=y,ttitle1=System,
     pvalues= y, pfoot=y, date=n,total=n,outdoc=~/consult/sharma/Tested.doc); 
     
     %table(dsn=data2, var=t1q1 t1q2 t1time t2q1 t2q2 t2time t3q1 t3q2 t3time favorite score score_w ,by=system, 
     type=1 1 1 1 1 1 1 1 1 2 1 1, labelwrap=y,ttitle1=System,
     pvalues= y, pfoot=y, date=n,total=n,outdoc=~/consult/sharma/Tested_D2R_vs_OTM.doc,pop=system lt 3); 
     
     %table(dsn=data2, var=t1q1 t1q2 t1time t2q1 t2q2 t2time t3q1 t3q2 t3time favorite score score_w ,by=system, 
     type=1 1 1 1 1 1 1 1 1 2 1 1, labelwrap=y,ttitle1=System,
     pvalues= y, pfoot=y, date=n,total=n,outdoc=~/consult/sharma/Tested_d2r_vs_rtf.doc,pop=system in (1 3)); 

     %table(dsn=data2, var=t1q1 t1q2 t1time t2q1 t2q2 t2time t3q1 t3q2 t3time favorite score score_w ,by=system, 
     type=1 1 1 1 1 1 1 1 1 2 1 1, labelwrap=y,ttitle1=System,
     pvalues= y, pfoot=y, date=n,total=n,outdoc=~/consult/sharma/Tested_otm_vs_rtf.doc, pop=system gt 1);      
     
     
     
     ods rtf file="~/consult/sharma/plots.rtf" style=journal;
     ods pdf file="~/consult/sharma/plots.pdf" notoc;
     
     proc sgplot data=data2 ; 
           
          vbar t1q1  / group=system missing groupdisplay=cluster grouporder=ascending    dataskin=pressed ;
     run;  
     proc sgplot data=data2; 
           
          vbar t1q2 / group=system missing groupdisplay=cluster grouporder=ascending   dataskin=pressed ;
     run;
     proc sgplot data=data2;
          vbox t1time / group=system  grouporder=ascending;
     run;
     proc sgplot data=data2; 
           
          vbar t2q1 / group=system missing groupdisplay=cluster grouporder=ascending   dataskin=pressed ;
     run;
     proc sgplot data=data2; 
           
          vbar t2q2 / group=system missing groupdisplay=cluster grouporder=ascending    dataskin=pressed ;
     run;
     proc sgplot data=data2;
          vbox t2time / group=system  grouporder=ascending;
     run;
     
     
     proc sgplot data=data2; 
           
          vbar t3q1 / group=system missing groupdisplay=cluster grouporder=ascending   dataskin=pressed ;
     run;
     proc sgplot data=data2; 
           
          vbar t3q2 / group=system missing groupdisplay=cluster grouporder=ascending   dataskin=pressed ;
     run;
     
     proc sgplot data=data2;
          vbox t3time / group=system  grouporder=ascending;
     run;

     proc sgplot data=data2;
          vbox score / group=system  grouporder=ascending;
     run;
     proc sgplot data=data2;
          vbox score_w / group=system  grouporder=ascending;
     run; 
     
     proc sgplot data=data2 (where=(favorite=1));
          vbar system / group=favorite missing groupdisplay=cluster grouporder=ascending   dataskin=pressed stat=percent;
      run;  
     ods pdf close;
     ods rtf close;
          
     
