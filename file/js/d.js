var CIFillForms = sz.ci.CIFillForms;
CIFillForms.prototype.goFirstPage = function() {
	var floatarea = this._getVirtualFloatArea();
	if (!floatarea) {
		return;
	}
	var dataFloatArea = floatarea.getDataFloatArea();
	var rowcount = dataFloatArea.getVisibleRowCount();
	if (rowcount == 0) {
		return;
	}
	var tableobj = floatarea.tableobj;
	tableobj.locateRow(0);
}
CIFillForms.prototype.goNextPage = function() {
	var floatarea = this._getVirtualFloatArea();
	if (!floatarea) {
		return;
	}
	var dataFloatArea = floatarea.getDataFloatArea();
	var rowcount = dataFloatArea.getVisibleRowCount();
	if (rowcount == 0) {
		return;
	}
	var tableobj = floatarea.tableobj;
	var is = tableobj.getInfiniteScroll();
	var lvindex = is.getLastVisibleRowIndex();
	
	var scrollPosition = is.scrollPosition;
	tableobj.locateRow(lvindex);
	is.scrollPosition = scrollPosition;
	is.handlerViewScroll();
}
CIFillForms.prototype.goPrevPage = function() {
	var floatarea = this._getVirtualFloatArea();
	if (!floatarea) {
		return;
	}
	var dataFloatArea = floatarea.getDataFloatArea();
	var rowcount = dataFloatArea.getVisibleRowCount();
	if (rowcount == 0) {
		return;
	}
	var tableobj = floatarea.tableobj;
	var is = tableobj.getInfiniteScroll();
	var fvindex = is.getFirstVisibleRowIndex();
	var lvindex = is.getLastVisibleRowIndex();
	var pageCount = lvindex - fvindex;
	var index = Math.max(0, fvindex - pageCount + 1);
	
	var scrollPosition = is.scrollPosition;
	tableobj.locateRow(index);
	is.scrollPosition = scrollPosition;
	is.handlerViewScroll();
}
CIFillForms.prototype.goLastPage = function() {
	var floatarea = this._getVirtualFloatArea();
	if (!floatarea) {
		return;
	}
	var dataFloatArea = floatarea.getDataFloatArea();
	var rowcount = dataFloatArea.getVisibleRowCount();
	if (rowcount == 0) {
		return;
	}
	var tableobj = floatarea.tableobj;
	tableobj.locateRow(rowcount - 1);
}

CIFillForms.prototype._getVirtualFloatArea = function() {
	var form = this.getCurrentForm();
	var floatAreas = form.getFloatAreas();
	var len = floatAreas ? floatAreas.length : 0;
	for (var i = 0; i < len; i++) {
		var floatArea = floatAreas[i];
		if (floatArea.isVirtual()) {
			return floatArea;
		}
	}
	return null;
}