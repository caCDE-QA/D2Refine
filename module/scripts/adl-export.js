ExporterManager.MenuItems.push(
		{
			"id" : "ADLExporter/export",
          	"label":"ADLADLADL",
          	"click": function() { ExporterManager.handlers.exportRows("adl", "adls");}
		}
);

ADLExporterMenuBar = {};

ADLExporterMenuBar.exportADL = function(format, ext) {

        alert(
            "This will export ADL..."
        );
};
