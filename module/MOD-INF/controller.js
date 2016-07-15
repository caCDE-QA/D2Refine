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

importPackage(edu.mayo.d2refine.services);
importPackage(edu.mayo.d2refine.services.commands);
importPackage(edu.mayo.d2refine.exporter.ADLExporterExtension);


var html = "text/html";
var encoding = "UTF-8";

// OpenRefine
var ClientSideResourceManager = Packages.com.google.refine.ClientSideResourceManager;
var RefineServlet = Packages.com.google.refine.RefineServlet;
var Exporters = Packages.com.google.refine.exporters;

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
            new ADLExporter("OPENEHR"));    
    Exporters.ExporterRegistry.registerExporter("adl_with_opencimi",
            new ADLExporter("OPENCIMI"));
    
    // Registering Commands
    RefineServlet.registerCommand(module, "registerD2RefineServices", new RegistrationCommand());
    RefineServlet.registerCommand(module, "reconcileWithCTS2", new ReconciliationCommand());
    
    // Script files to inject into /project page
    ClientSideResourceManager.addPaths(
            "project/scripts", 
            module, [
            "scripts/d2rReconciliation.js",
            "scripts/utils/util.js",
            "scripts/exporter/model-export-menu.js",
            "scripts/extension-bar-menu.js",
            "dialogs/d2rAddReconService.js",
            "dialogs/d2rAbout.js" ]);

    // Style files to inject into /project page
    ClientSideResourceManager.addPaths(
            "project/styles", 
            module,
            [            
              "styles/project-injection.less",
              "styles/d2refine.less",
              "dialogs/d2rDialogs.less",
              "dialogs/d2rAbout.less"
            ]);
}

function process(path, request, response) 
{
    logger.info(">>>>> Received D2Refine process Request");
    
    logger.info("path=" + path);
    logger.info("request.method=" + request.getMethod());
    logger.info("request.method=" + request.getParameter("columnName"));
        
    if (path.match(/^services\/main/g))
    {
        logger.info("Calling Handle Request");
        var jsonResponse = D2RefineServiceManager.instance().handle(path, request);
        
        logger.info("Response:" + jsonResponse);
        
        if(jsonResponse)
        {
            logger.info(jsonResponse);
            butterfly.sendString(request, response, jsonResponse ,"UTF-8", "text/javascript");
            return;
        }
        else
        {
            butterfly.sendError(request, response, 404, "unknown service");
        }
    }
    
    if (path == "/" || path == "") 
    {
        butterfly.redirect(request, response, "index.html");
    }
}
