function CTS2QueryPanel(column, service, container) {
  this._column = column;
  this._service = service;
  this._container = container;

  this._constructUI();
}

CTS2QueryPanel.prototype.activate = function() {
  this._panel.show();
};

CTS2QueryPanel.prototype.deactivate = function() {
  this._panel.hide();
};

CTS2QueryPanel.prototype.dispose = function() {
  this._panel.remove();
  this._panel = null;

  this._column = null;
  this._service = null;
  this._container = null;
};

CTS2QueryPanel.prototype._constructUI = function() {
  var self = this;
  this._panel = $(DOM.loadHTML("d2refine", "dialogs/cts2-query-panel.html")).appendTo(this._container);
  this._elmts = DOM.bind(this._panel);
  
  this._elmts.cts2_contain.html($.i18n._('core-recon')["cell-contains"]);
  // put more fields here
  
  this._wireEvents();
};

CTS2QueryPanel.prototype._wireEvents = function() {
  var self = this;
 // this._elmts.strictNamespaceInput
  //.suggest({ filter : '(all type:/type/namespace)' })
  //.bind("fb-select", function(e, data) {
    //self._panel.find('input[name="recon-dialog-strict-choice"][value="key"]').attr("checked", "true");
    //self._panel.find('input[name="recon-dialog-strict-namespace-choice"][value="other"]').attr("checked", "true");
  //});
};


CTS2QueryPanel._batchSearch = function(queries, onDone) {
	  var keys = [];
	  for (var n in queries) {
	    if (queries.hasOwnProperty(n)) {
	      keys.push(n);
	    }
	  }

	  var result = {};
	  var args = [];
	  var makeBatch = function(keyBatch) {
	    var batch = {};
	    for (var k = 0; k < keyBatch.length; k++) {
	      var key = keyBatch[k];
	      batch[key] = queries[key];
	    }

	    // TODO: New API doesn't accept multiple queries
//	    args.push("https://www.googleapis.com/freebase/v1/search?key=" + Freebase.API_KEY + "&" + 
	    args.push("http://api.freebase.com/api/service/search?" + // FIXME:
	        $.param({ "queries" : JSON.stringify(batch) }) + "&callback=?");

	    args.push(null); // no data
	    args.push(function(data) {
	      for (var k = 0; k < keyBatch.length; k++) {
	        var key = keyBatch[k];
	        result[key] = data[key];
	      }
	    });
	  };

	  for (var i = 0; i < keys.length; i += 10) {
	    makeBatch(keys.slice(i, i + 10));
	  }

	  args.push(function() {
	    onDone(result);
	  });

	  //Ajax.chainGetJSON.apply(null, args);
	};

CTS2QueryPanel._cleanName = function(s) {
	  return s.replace(/\W/g, " ").replace(/\s+/g, " ").toLowerCase();
	};
	
CTS2QueryPanel.prototype.start = function() {
  var bodyParams;
  /*
  var match = $('input[name="recon-dialog-strict-choice"]:checked')[0].value;
  if (match == "key") {
    var namespaceChoice = $('input[name="recon-dialog-strict-namespace-choice"]:checked')[0];
    var namespace;

    if (namespaceChoice.value == "other") {
      var suggest = this._elmts.strictNamespaceInput.data("data.suggest");
      if (!suggest) {
        alert($.i18n._('core-recon')["specify-ns"]);
        return;
      }
      namespace = {
        id: suggest.id,
        name: suggest.name
      };
    } else {
      namespace = {
        id: namespaceChoice.value,
        name: namespaceChoice.getAttribute("nsName")
      };
    }

    bodyParams = {
      columnName: this._column.name,
      config: JSON.stringify({
        mode: "freebase/strict",
        match: "key",
        namespace: namespace
      })
    };
  } else if (match == "id") {
    bodyParams = {
      columnName: this._column.name,
      config: JSON.stringify({
        mode: "freebase/strict",
        match: "id"
      })
    };
  } else if (match == "guid") {
    bodyParams = {
      columnName: this._column.name,
      config: JSON.stringify({
        mode: "freebase/strict",
        match: "guid"
      })
    };
  }
  */
  
  var columns = theProject.columnModel.columns;
  
  var typedCandidates = [];
  var candidates = [];

  for (var c = 0; c < columns.length; c++) {
    var column = columns[c];
    var typed = (column.reconConfig) && 
    ReconciliationManager.isFreebaseIdOrMid(column.reconConfig.identifierSpace) &&
    ReconciliationManager.isFreebaseId(column.reconConfig.schemaSpace);

    var candidate = {
      status: "unbound",
      typed: typed,
      index: c,
      column: column
    };

    candidates.push(candidate);
    if (typed) {
      typedCandidates.push(candidate);
    }
  }
  
  if (typedCandidates.length > 0) {

  } else {
    var queries = {};
    for (var i = 0; i < candidates.length; i++) {
      var candidate = candidates[i];
      var name = SchemaAlignment._cleanName(candidate.column.name);
      var key = "t" + i + ":search";
      queries[key] = {
        "query" : name,
        "limit" : 10,
        "type" : "/type/type,/type/property",
        "type_strict" : "any"
      };
    }

    CTS2QueryPanel._batchSearch(queries, function(result) {
      console.log(result);
    });
  }
  bodyParams = {
	      columnName: this._column.name,
	      config: JSON.stringify({
	      //mode: "d2refine",
	        service: this._service.url,
	      })
	    };
  
  $.post("extension/d2refine/services/main", bodyParams,function(data){
      //TermReconciliationManager.registerService(data);
          if(onDone){
                  onDone();
          }
  },"json");
  
  /*
  Refine.postProcess(
    "d2refine",
    "reconcileWithCTS2",
    {},
    bodyParams,
    { cellsChanged: true, columnStatsChanged: true }
  );*/
};