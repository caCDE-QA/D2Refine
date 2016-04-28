/*

Copyright 2010, Google Inc. 
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

 * Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above
copyright notice, this list of conditions and the following disclaimer
in the documentation and/or other materials provided with the
distribution.
 * Neither the name of Google Inc. nor the names of its
contributors may be used to endorse or promote products derived from
this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,           
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY           
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

var html = "text/html";
var encoding = "UTF-8";

// OpenRefine
var ClientSideResourceManager = Packages.com.google.refine.ClientSideResourceManager;
var RefineServlet = Packages.com.google.refine.RefineServlet;
var Exporters = Packages.com.google.refine.exporters;

// D2Refine
var D2RefinePkg = Packages.edu.mayo.d2refine;
var D2ADLExpPkg = D2RefinePkg.exporter.ADLExporterExtension;
var D2ReconPkg = D2RefinePkg.reconciliation;


var LF = Packages.org.slf4j.LoggerFactory;
var logger = LF.getLogger("d2refine");

/*
 * Function invoked to initialize the extension.
 */
 
function init() 
{   
    logger.info("Initializing D2Refine Extensions...");
    
    // Registering Exporters
    Exporters.ExporterRegistry.registerExporter("adl_with_openehr",
            new D2ADLExpPkg.ADLExporter("OPENEHR"));    
    Exporters.ExporterRegistry.registerExporter("adl_with_opencimi",
            new D2ADLExpPkg.ADLExporter("OPENCIMI"));
    
    // Registering Commands
    RefineServlet.registerCommand(module, "term-reconcile", 
            new D2ReconPkg.TermReconciliationService());
    
    // Script files to inject into /project page
    ClientSideResourceManager.addPaths(
            "project/scripts", 
            module, [
            "scripts/config.js", 
            "scripts/exporter/model-export-menu.js",
            "scripts/extension-bar-menu.js", 
            "dialogs/about.js" ]);

    // Style files to inject into /project page
    ClientSideResourceManager.addPaths(
            "project/styles", 
            module,
            [ 
              "styles/project-injection.less",
              "styles/d2r-dialogs.less",
              "styles/about.less"
            ]);
}
