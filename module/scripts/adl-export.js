ExporterManager.MenuItems.push({});//add separator
ExporterManager.MenuItems.push(
		{
			"id" : "D2Refine/export",
          	"label":"ADL2.0 Export",
          	"click": function() { ExporterManager.handlers.exportRows("D2Refine", "adls");}
		}
);