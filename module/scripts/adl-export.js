ExporterManager.MenuItems.push({});//add separator
ExporterManager.MenuItems.push(
		{
			"id" : "D2Refine1/export",
          	"label":"ADL2.0 Export with OpenEHR RM",
          	"click": function() { ExporterManager.handlers.exportRows("D2Refine1", "adls");}
		},
		{
			"id" : "D2Refine2/export",
          	"label":"ADL2.0 Export with OpenCIMI RM",
          	"click": function() { ExporterManager.handlers.exportRows("D2Refine2", "adls");}
		}
);