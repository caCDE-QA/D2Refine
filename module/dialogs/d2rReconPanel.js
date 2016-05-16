function D2RefineReconPanel(column, service, container) {
  this._column = column;
  this._service = service;
  this._container = container;
  this._constructUI();
}

D2RefineReconPanel.prototype._constructUI = function() {
  this._panel = $(DOM.loadHTML("d2refine", "dialogs/d2rReconPanel.html")).appendTo(this._container);
  this._elmts = DOM.bind(this._panel);
};

D2RefineReconPanel.prototype.activate = function() {
  this._panel.show();
};

D2RefineReconPanel.prototype.deactivate = function() {
  this._panel.hide();
};

D2RefineReconPanel.prototype.dispose = function() {
  this._panel.remove();
  this._panel = null;

  this._column = null;
  this._service = null;
  this._container = null;
};

D2RefineReconPanel.prototype.start = function() {
  Refine.postCoreProcess(
    "reconcile",
    {},
    {
      columnName: this._column.name,
      config: JSON.stringify({
        mode: "standard-service",
        service: this._service.url,
      })
    },
    { cellsChanged: true, columnStatsChanged: true }
  );
};