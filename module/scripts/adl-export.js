ExporterManager.MenuItems.push({});//add separator
ExporterManager.MenuItems.push(
		{
			"id" : "ADLExporter/export",
          	"label":"ADL2.0 Export",
          	"click": function() { ExporterManager.handlers.exportRows("adl", "adls");}
		}
);

ADLExporterMenuBar = {};

ADLExporterMenuBar.exportADL = function(format, ext) {

        alert(
            "This will export ADL..."
        );
};
