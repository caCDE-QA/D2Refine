# D2Refine
D2Refine is an extension to OpenRefine (formerly Google Refine) to convert restrictions (specified in a spreadsheet like interface) into a clinical model.  

D2Refine currently serializes dbGap Data Dictionary Template into the OpenEHR's Archetype Definition Lanugage (ADL) 2.0.

D2Refine leverages the functionality provided by following libraries and provides functionality that extends the OpenRefine's Export menu.

* ADL 2 Core (https://github.com/openEHR/adl2-core)
Provides libaries for ADL Parser, Archetype Model, Reference Model, OpenEHR Reference Model Implementation.
* minCIMI Reference Model (https://github.com/semantix/model-rm-opencimi). 
This augments ADL2 Core libraries with an additonal Reference Model implementation of miniCIMI RM.
* ADLWorks (https://github.com/semantix/ADLWorks)
This library provides a layer of convenience methods to create ADL artefacts in memory.

![alt tag](https://github.com/caCDE-QA/D2Refine/blob/master/docs/img/D2Refine.png)

## Installation

1. Install OpenRefine. D2Refine is a set of plugins to OpenRefine. Please note installaing this extension to Google Refine (a predecessor of OpenRefine may not work properly). OpenRefine could be downloaded from its download page: https://github.com/OpenRefine/OpenRefine/releases/tag/2.6-beta.1
2. Find the location of OpenRefine's "extensions" folder. You can find out by clicking on the link "Browse workspace directory" on OpenRefine's project list. [Usually on webpage "http://127.0.0.1:3333", when OpenRefine is running].  There should be an "extensions" folder in the workspace directory. Create the 'extensions" folder manually, if it does not exist.
3. Download file "D2Refine.zip" and copy the content to the OpenRefine's "extensions" folder.
4. Restart OpenRefine.

## Development Environment

1. Download and setup OpenRefine following instructions at "https://github.com/OpenRefine/OpenRefine/wiki/Developers-Guide".
2. Copy this project's contents into OpenRefine's SIMILE Butterfly project's "extensions" folder.
3. Tweak build.xml file for your environment if needed.


