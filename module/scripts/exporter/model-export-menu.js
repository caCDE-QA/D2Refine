ExporterManager.MenuItems.push({});//add separator
ExporterManager.MenuItems.push(
{
    "id" : "adl_with_openehr",
    "label":"ADL2.0 Export with OpenEHR RM",
    "click": function() { 
        ExporterManager.handlers.exportRows("adl_with_openehr", "adls");
        }
},
{
    "id" : "adl_with_opencimi",
    "label":"ADL2.0 Export with OpenCIMI RM",
    "click": function() {
        ExporterManager.handlers.exportRows("adl_with_opencimi", "adls");
        }
}
);