D2Refine
========
D2Refine is a clinical study metadata harmonization and validation workbench based on 
[OpenRefine](http://openrefine.org/) (formerly Google Refine).  It leverages the simple 
spreadsheet like interface of OpenRefine to describe clinical models as row and columns. The dataset definition is represented as list of variables and the restrictions or constraints on them.  
D2Refine platform includes a collection of the OpenRefine plugins that augment OpenRefine with following capabilities:  
- Terminology Binding - Metadata reconciliation services leveraging the [Common Terminology Services 2 (CTS2)] (http://informatics.mayo.edu/cts2/index.php/Main_Page).
- Transformation into Standard Model - Transform listed restrictions of a data dictionary into a standardized clinical model. Currently it converts data dictionary definitions into [Archetype Definition Language (ADL)](http://www.openehr.org/releases/AM/latest/docs/ADL2/ADL2.html) format of [CIMI](http://www.opencimi.org/).  The Transformation to another CIMI approved formalism of [Archetype Modeling Language (AML)](http://www.omg.org/spec/AML) is in progress. 

More Information can be found at [D2Refine Wiki](D2Refine/wiki)

![alt tag](https://github.com/caCDE-QA/D2Refine/blob/master/docs/img/D2Refine.png)
