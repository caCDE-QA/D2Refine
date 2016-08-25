D2Refine
========
D2Refine is a clinical study metadata harmonization and validation workbench based on 
[OpenRefine](http://openrefine.org/) (formerly Google Refine).  It leverages the simple 
spreadsheet like interface of OpenRefine to describe clinical models as row and columns. The dataset definition is represented as list of variables and the restrictions or constraints on them.  
D2Refine platform includes a collection of the OpenRefine plugins that augment OpenRefine with following capabilities:  
- Terminology Binding - Metadata reconciliation services leveraging the [Common Terminology Services 2 (CTS2)] (http://informatics.mayo.edu/cts2/index.php/Main_Page).
- Transformation into Standard Model - Transform listed restrictions of a data dictionary into a standardized clinical model. Currently it converts data dictionary definitions into [Archetype Definition Language (ADL)](http://www.openehr.org/releases/AM/latest/docs/ADL2/ADL2.html) format of [CIMI](http://www.opencimi.org/).  The Transformation to another CIMI approved formalism of [Archetype Modeling Language (AML)](http://www.omg.org/spec/AML) is in progress.   

D2Refine employs the functionality provided by following libraries and provides functionality that extends the OpenRefine's Export menu.

- ADL 2 Core (https://github.com/openEHR/adl2-core)
Provides libaries for ADL Parser, Archetype Model, Reference Model, OpenEHR Reference Model Implementation.
- [CIMI Reference Model] (https://github.com/semantix/model-rm-opencimi). CIMI RM implementation augments ADL2 Core libraries, which comes with OpenEHR Reference Model.
- [ADLWorks] (https://github.com/semantix/ADLWorks) - a library provides a layer of convenience methods for easier ADL serialization.

![alt tag](https://github.com/caCDE-QA/D2Refine/blob/master/docs/img/D2Refine.png)

Installation
------------
1. Install OpenRefine. OpenRefine could be downloaded from [here](https://github.com/OpenRefine/OpenRefine/releases/tag/2.6-beta.1)
2. Find the location of OpenRefine's "extensions" folder.
  - You can find out by clicking on the link "Browse workspace directory" on OpenRefine's project list, when OpenRefine is running at "http://127.0.0.1:3333".
  - There should be an "extensions" folder in the workspace directory. Create the 'extensions" folder manually, if it does not exist.
3. Download the file "D2Refine.zip" [above](https://github.com/caCDE-QA/D2Refine/raw/master/D2Refine.zip) and extract the contents to the OpenRefine's "extensions" folder.
4. Restart OpenRefine.

Configuring Development Environment
-----------------------------------
1. Follow the OpenRefine Setup instructions in [Developer's Guide](https://github.com/OpenRefine/OpenRefine/wiki/Developers-Guide).
2. Copy this project's contents into OpenRefine's SIMILE Butterfly Project's (in IDE of your choice) "extensions" folder.
3. Tweak build.xml file for your environment for 'build' and 'clean' ant tasks. 
