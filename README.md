# D2Refine
D2Refine is an extension to OpenRefine (formerly Google Refine) to convert restrictions (specified in a spreadsheet like interface) into a clinical model.  

D2Refine currently serializes the model into the OpenEHR's Archetype Definition Lanugage (ADL) 2.0.

D2Refine leverages the functionality provided by following libraries and provides functionality that extends the OpenRefine's Export menu.

* ADL 2 Core (https://github.com/openEHR/adl2-core)
Provides libaries for ADL Parser, Archetype Model, Reference Model, OpenEHR Reference Model Implementation.
* minCIMI Reference Model (https://github.com/semantix/model-rm-opencimi). 
This augments ADL2 Core libraries with an additonal Reference Model implementation of miniCIMI RM.
* ADLWorks (https://github.com/semantix/ADLWorks)
This library provides a layer of convenience methods to create ADL artefacts in memory.

![alt tag](https://github.com/caCDE-QA/D2Refine/blob/master/docs/img/D2Refine.png)




