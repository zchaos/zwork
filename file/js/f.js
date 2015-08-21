// ============= 提供的接口 ================
floatarea.getFloatViewMode();//浮动显示方式:onetable:用一个表格显示;nested:大表格嵌套小表格；vtable:虚拟表格
floatarea.isFloatRows();
floatarea.getTopCrossFloatArea();
floatarea.getOwnerRow();
floatarea.getRect();
floatarea.getRectComponentInfs();//返回[[{compinf:xxx,rowspan:xxx,colspan:xxx,rowid:xxx}],[]]
floatarea.getStruct(startRow,endRow);//虚拟表格使用，获得startRow到endRow之间行的结构信息,返回的内容:[[{compinf:xxx,rowspan:xxx,colspan:xxx,rowid:xxx}],[]]
floatarea.compareStruct(struct1,struct2);//比较两个结构的区别，返回的信息包括:删除行，增加行，属性调整信息
row.getFloatAreas();
row.getFloatArea(floatName);
componentdata.getOwnerRow();
componentdata.getCell(row,col);//复制粘贴
componentdata.getRowSpan();
componentdata.getColSpan();
// =============================一个表格显示浮动或嵌套显示
// --------- 刷新整表 ---------
CIForm.prototype.refrsh = function() {
  var componentinfs = formdata.getComponentInfs();
  for (var i = 0; i < componentinfs.length; i++) {
    var componentdata = formdata.cd(componentinfs[i]);
    this.refreshComponent(componentdata);
  }
  var floatareas = formdata.getFloatAreas();
  for (var i = 0; i < floatareas.length; i++) {//先删除行
    var dataFloatarea = floatareas[i];
    var floatarea = this.findFloatArea(dataFloatArea);// 找到界面上对应的浮动区域
    floatarea.synRemoveRows(dataFloatarea);
  }

  for (var i = 0; i < floatareas.length; i++) {//新增行并更新数据
    var dataFloatarea = floatareas[i];
    var floatarea = this.findFloatArea(dataFloatArea);// 找到界面上对应的浮动区域
    floatarea.synAddRows(dataFloatarea);
    floatarea.refreshComponent(dataFloatarea);
  }
}
CIForm.prototype.findFloatArea = function(dataFloatarea) {
  var getComponentBelongRow = function(row) {
    var floatarea = row.getFloatArea();
    var prow = floatarea.getOwnerRow();
    if (prow) {
      var r = getComponentBelongRow(prow);
      var fa = r.getFloatArea(floatarea.floatName);
      return fa.getRowBySN(row.getSN());
    }
    var fa = this.getFloatArea(floatarea.floatName);
    return fa.getRowBySN(prow.getSN());
  }
  var prow = dataFloatarea.getOwnerRow();
  if (prow) {
    var r = getComponentBelongRow(prow);
    return r.getFloatArea(floatarea.floatName);
  }
  return this.getFloatArea(dataFloatarea.floatName);
}

CIForm.prototype.findComponent = function(componentdata) {
  var compinf = componentdata.compinf;
  var compid = compinf.name;
  var formdata = componentdata.getFormData();
  var row = componentdata.getOwnerRow();
  if (row) {
    var fa = this.findFloatArea(row.getFloatArea());
    var r = fa.getRowBySN(row.getSN());
    return r.getComponent(compid);
  }
  return this.getComponent(compid);
}
// --------- 刷新浮动区域 ---------
CIFloatArea.prototype.refresh = function(dataFloatarea) {
  this.synRemoveRows(dataFloatarea);
  this.synAddRows(dataFloatarea);
  this.refreshComponent(dataFloatarea);
}

CIFloatArea.prototype.synRemoveRows = function(dataFloatarea) {
  var rowcount = dataFloatarea.getRowCount();
  var rcount = floatarea.getRowCount();
  if (rowcount < rcount) {
    floatarea.removeRow(rcount - rowcount);// 删除行
  }
  for(var i=0;i<rcount;i++){
    var row = floatarea.getRow(i);
    var dataRow = dataFloatarea.getRow(i);
    row.synRemoveRows(dataRow);
  }
}
CIFloatArea.prototype.synAddRows = function(dataFloatarea) {
  var rowcount = dataFloatarea.getRowCount();
  var rcount = floatarea.getRowCount();
  for(var i=0;i<rowcount-rcount;i++){
    this.addRow();
  }
}
CIFloatArea.prototype.refreshComponent = function(dataFloatarea) {
  var rowcount = this.getRowCount();
  for(var i=0;i<rowcount;i++){
    var row = this.getRow(i);
    var dataRow = dataFloatarea.getRow(i);
    row.refreshComponent(dataRow);
  }
}

CIFloatAreaRow.prototype.refresh = function(dataRow) {
  var componentinfs = dataRow.getComponentInfs();
  for (var i = 0; i < componentinfs.length; i++) {
    var componentdata = dataRow.cd(componentinfs[i]);
    var cicomp = this.ciform.findComponent(componentdata);// 找到界面上对应的控件
    cicomp.setText(componentdata.getText());
  }

  var floatareas = dataRow.getFloatAreas();
  for (var i = 0; i < floatareas.length; i++) {
    var floatarea = floatareas[i];
    var dataFloatArea = floatarea.getDataFloatArea();
    floatarea.refresh(dataFloatArea);
  }
}
CIFloatAreaRow.prototype.synRemoveRows = function(dataRow) {
  var floatareas = this.getFloatAreas();
  var dataFloatAreas = dataRow.getFloatAreas();
  for(var i=0;i<floatareas.length;i++){
    var floatarea = floatareas[i];
    var dataFloatArea = dataRow.getFloatArea(floatarea.getName());
    floatarea.synRemoveRows(dataFloatArea);
  }
}
CIFloatAreaRow.prototype.refreshComponent = function(dataRow) {
  var compinfs = dataRow.getComponentInfs();
  for(var i=0;i<compinfs.length;i++){
    var compinf = compinfs[i];
    var cicomp = this.getComponent(compinf);
    var componentdata = dataRow.cd(compinf);
    this.ciform.refreshComponent(componentdata);
  }

  var floatareas = this.getFloatAreas();
  for(var i=0;i<floatareas.length;i++){
    var floatarea = floatareas[i];
    var dataFloatarea = dataRow.getFloatArea(floatarea.getName());
    floatarea.refreshComponent(dataFloatarea);
  }
}

// --------- 增加行 ---------
CIFloatArea.prototype.addRow = function() {
  var dataFloatArea = this.getDataFloatArea();
  var cinfs = dataFloatArea.getRectComponentInfs();
  var cachedoms = this.cachedoms;
  var clen = cinfs.length;
  var rlen = cinfs[0].length;

  var doms = [];
  for(var i=rlen;i<rlen;i++){
    var cinf = cinfs[i][0];
    var rowdom = cachedoms[cinf.rowid].clone();
    for(var j=1;j<clen;j++){
      var cinf = cinf[i][j];
      var cell = cachedoms[cinf.compinf.name].clone();
      cell.setAttribute("rowspan",cinf.rowspan);
      cell.setAttribute("colspan",cinf.colspan);
      rowdom.appendChild(cell);
    }
  }

  var tabdom = this.tbody;
  for(var i=0;i<doms.length;i++){
    tabdom.appendChild(doms[i]);
  }
  //会在refreshformevent事件中调整合并单元格
}

// --------- 删除行 ---------
CIFloatArea.prototype.removeRow = function() {
  this.$doms.remove();
  // 会在refreshformevent事件中调整合并单元格
}
// --------- 粘贴 ---------
CIFillDataMgrPrototype.paste = function(datas, formName, tabid, startRow, startCol, options) {
  // datas为二位数组
  var clen = datas.length;
  var rlen = datas[0].length;
  var formdata = this.getFormData(formName);
  var tabdata = formdata.getComponent(tabid);
  var celldata = tabdata.getCell(startRow,startCol);
  var isFloat = celldata.getOwnerRow()!=null;
  if (!isFloat) {
    for (var i = 0; i < rlen; i++) {
      for (var j = 0; j < clen; j++) {
        var componentdata = tabdata.getCell(startRow+i,startCol+j);
        componentdata.setValue(datas[i][j]);
      }
    }
  }
  else {
    var floatarea = celldata.getOwnerRow().getFloatArea();
    if (options == "new") {// 新插入行粘贴数据
      for (var i = 0; i < rlen; i++) {
        var row = floatarea.newRow();
        for (var j = 0; j < clen; j++) {
          var componentdata = tabdata.getCell(startRow+i,startCol+j);
        componentdata.setValue(datas[i][j]);
        }
      }
    }
    else if (options == "override") {// 从当前行开始覆盖;
      for (var i = 0; i < rlen; i++) {
        for (var j = 0; j < clen; j++) {
          var componentdata = tabdata.getCell(startRow+i,startCol+j);
          componentdata.setValue(datas[i][j]);
        }
      }
    }
    else if (options == "newAndOverrideKey") {// 有相同的key的，则覆盖，如果没有，则新增行
      // TODO 算法比较复杂
    }
  }
}
// --------- 拷贝 ---------
CIFillDataMgrPrototype.paste = function(formName, tabid, startRow, startCol, endRow, endCol) {
  var datas = [];
  for (var i = startRow; i < endRow; i++) {
    var rowData = datas[i] = [];
    for (var j = startCol; j < endCol; j++) {
      var value = xxx;
      rowData.push(value);
    }
  }
  return datas;
}
// --------- 增量刷新 ---------
CIForm.prototype.refreshComponent = function(componentdata) {
  var formdata = this.getFormData();
  {// 设置控件值
    var componentdata = formdata.cd(componentinfs[i]);
    var cicomp = this.getComponent(componentdata);// 找到界面上对应的控件
    cicomp.setText(componentdata.getText());
  }
  {// 删除行
    var floatarea = xxx;
    floatarea.removeRow();
  }
  {// 增加行
    var floatarea = xxx;
    floatarea.addRow();
  }
  {//调整单元格合并行列
    var rowspan = componentdata.getRowSpan();
    var colspan = componentdata.getColSpan();
    var cicomp = this.getComponent(componentdata);
    cicomp.setRowColSpan(rowspan,colspan);
  }
}
CIForm.prototype.refreshComponentProperty = function(componentdata) {
  var cicomp = this.getComponent(componentdata.compinf.name);
  var p = componentdata.p;
  if(p == "rowspan"){
    cicomp.setRowColSpan(componentdata.getRowSpan());
  }
  else if（p == "colspan"){
    cicomp.setRowColSpan(null,componentdata.getColSpan());
  }
  else if(p == null || p == "value"){
    cicomp.setText(componentdata.getText());
  }
}

// =============================虚拟表格嵌套显示浮动
// --------- 刷新整表 ---------
CIFloatArea.prototype.syndata = function(startRow,endRow) {
  var oldStruct = this.oldStruct;
  if(oldStruct==null){
    this.synNewdata(startRow,endRow);
    return;
  }
  var newStruct = floatarea.getStruct(startRow,endRow);
  var changeStruct = floatarea.compareStruct(oldStruct,newStruct);

  //删除行
  var removeRows = changeStruct.removeRows;
  for(var i=0;i<removeRows.length;i++){
    this.removeRowBySN(removeRows[i]);
  }

  //增加行
  var addRows = changeStruct.addRows;
  for(var i=0;i<addRows.length;i++){
    this.newRow(addRows.index,addRows.cinfs);
  }

  //刷新属性
  var changeComps = changeStruct.changeComps;
  for(var i=0;i<changeComps.length;i++){
    this.ciform.refreshComponentProperty(changeComps[i]);
  }
  this.oldStruct = newStruct;
}
CIFloatArea.prototype.synNewdata = function(startRow, endRow) {
  var newStruct = floatarea.getStruct(startRow, endRow);
  var cinfs = newStruct.cinfs;

  var doms = [];
  var rlen = cinfs.length;
  for (var i = rlen; i < rlen; i++) {
    var cinf = cinfs[i];
    var clen = cinf.length;
    var rowdom = cachedoms[cinf[0].rowid].clone();
    for (var j = 1; j < clen; j++) {
      var cinf = cinf[i][j];
      var cell = cachedoms[cinf.compinf.name].clone();
      cell.setAttribute("rowspan", cinf.rowspan);
      cell.setAttribute("colspan", cinf.colspan);
      rowdom.appendChild(cell);
    }
  }

  var tabdom = this.tbody;
  for (var i = 0; i < doms.length; i++) {
    tabdom.appendChild(doms[i]);
  }
  this.oldStruct = newStruct;
}

// --------- 增加行 ---------
CIFloatArea.prototype.newRow = function(index, cinfs) {
  var doms = [];
  var rlen = cinfs.length;
  for (var i = rlen; i < rlen; i++) {
    var cinf = cinfs[i];
    var clen = cinf.length;
    var rowdom = cachedoms[cinf[0].rowid].clone();
    for (var j = 1; j < clen; j++) {
      var cinf = cinf[i][j];
      var cell = cachedoms[cinf.compinf.name].clone();
      cell.setAttribute("rowspan", cinf.rowspan);
      cell.setAttribute("colspan", cinf.colspan);
      rowdom.appendChild(cell);
    }
  }

  var tabdom = this.tbody;
  for (var i = 0; i < doms.length; i++) {
    tabdom.insert(index,doms[i]);
  }
}
// --------- 删除行 ---------
CIFloatArea.prototype.removeRowBySN = function(sn) {
  var $row = this.getRowBySN(sn);
  $row.$doms.remove();
}
// --------- 粘贴 ---------
// --------- 拷贝 ---------
// --------- 增量刷新 ---------
