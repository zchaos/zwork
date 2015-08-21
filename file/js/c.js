//TODO 全量刷新，增量刷新(增加行，删除行)，复制粘贴，虚拟表格
/* ============== 接口 =================== */
// 返回浮动区域，type指定浮动区域的类型。type==undefind，返回所有；type=cross，返回交叉浮动；type=top，返回顶级浮动(不属于另外一个浮动);type=virtual,返回虚拟表格对应的浮动区域
FormData.prototype.getFloatAreas = function(type) {
}
// 获得浮动区域，如果传入了2个floatName，则获得交叉浮动
FormData.prototype.getFloatArea = function(floatName1, floatName2) {
}
// 是否交叉浮动区域
FloatAreaData.prototype.isCross = function() {
};
// 返回上级浮动区域，如果是横纵向交叉，则会返回2个，否则只会返回一个
FloatAreaData.prototype.getParentFloatAreaDatas = function() {
};
// 获得下级浮动区域
FloatAreaData.prototype.getSubFloatAreaDatas = function() {
};
// 获得上级浮动行，如果是横纵向交叉，则会返回2个，否则只会返回一个
FloatAreaDataRow.prototype.getParentRows = function() {

}
/* ============== 调用 =================== */
CIForm.prototype.refresh = function() {
	var formdata = xxx;
	var componentinfs = formdata.getComponentInfs();
	var len = componentinfs == null ? 0 : componentinfs.length;
	for (var i = 0; i < len; i++) {
		var compinf = componentinfs[i];
		var componentdata = formdata.cd(compinf);
		this.refreshComponent(componentdata);
	}

	var isVirtual = xxx;
	if (isVirtual) {
		var floatAreas = formdata.getFloatAreas("virtual");
		this.refreshVirtualFloatArea(floatAreas);
	}
	else {
		// 刷新顶级浮动区域，可能会增删行
		var floatAreas = formdata.getFloatAreas("top");
		var len = floatAreas == null ? 0 : floatAreas.length;
		for (var i = 0; i < len; i++) {
			this.refreshFloatArea(floatAreas[i]);
		}

		// 刷新交叉浮动
		var floatAreas = formdata.getFloatAreas("cross");
		var len = floatAreas == null ? 0 : floatAreas.length;
		for (var i = 0; i < len; i++) {
			this.refreshCrossFloatArea(floatAreas[i]);
		}
	}
};

CIForm.prototype.refreshFloatArea = function(floatArea) {
	if (floatArea.isCross()) {// 交叉浮动不在此方法中处理。必须所有浮动区域增删完成后，才刷新交叉浮动。否则界面上行还没有增加，交叉浮动控件会找不到对应的界面元素
		return;
	}
	var rows = floatArea.getVisibleRows();
	var len = rows.length;
	for (var i = 0; i < len; i++) {
		var row = xxx;// FloatAreaDataRow
		var cirow = this.getFloatAreaRow(row);//
		if (cirow == null) {
			// TODO 插入行
		}
		this.refreshFloatAreaRow(cirow, row);
	}

	var floatAreas = formdata.getSubFloatAreaDatas();
	var flen = floatAreas.length;
	for (var i = 0; i < flen; i++) {
		this.refreshFloatArea(floatAreas[i]);
	}
};

// 刷新交叉浮动
CIForm.prototype.refreshCrossFloatArea = function(floatArea) {
	var rows = floatArea.getVisibleRows();
	var len = rows.length;
	for (var i = 0; i < len; i++) {
		var row = xxx;// FloatAreaDataRow
		var cirow = this.getFloatAreaRow(row);//
		this.refreshFloatAreaRow(cirow, row);
	}
};
CIForm.prototype.refreshFloatAreaRow = function(row, row) {
	var componentinfs = row.getComponentInfs();
	var len = componentinfs == null ? 0 : componentinfs.length;
	for (var i = 0; i < len; i++) {
		var compinf = componentinfs[i];
		var componentdata = row.cd(compinf);
		this.refreshComponent(componentdata);
	}
};
/**
 * 返回界面上的浮动区域对象
 * @param {} floatArea
 */
CIForm.prototype.getFloatAreaRow = function(row) {
	var floatName = row.getFloatAreaData().floatName;
	var sn = row.getSN();

	var rows = row.getParentRows();
	var len = rows.length;
	if (len == 0) {
		return this.getFloatAreaRow(floatName, sn);
	}
	if (len == 1) {
		var prow = rows[0];
		var pfloatName = prow.getFloatAreaData().floatName;
		var psn = prow.getSN();
		return this.getFloatAreaRow(pfloatName, psn, floatName, sn);
	}
	var prow1 = rows[0];
	var prow2 = rows[1];
	var pfloatName1 = prow1.getFloatAreaData().floatName;
	var psn1 = prow1.getSN();
	var pfloatName2 = prow2.getFloatAreaData().floatName;
	var psn2 = prow2.getSN();
	return this.getFloatAreaRow(pfloatName1, psn1, pfloatName2, psn2, floatName, sn);
};

CIForm.prototype.refreshVirtualFloatArea = function(floatarea) {
	var vfa = xxx;// 生成虚拟表格显示的信息
};