function onAfterReportCalc(args) {
	var result = args.result;
	var tableComp = result.getComponent("rpt1");
	var rowcount = tableComp.getRowCount();
	var colcount = tableComp.getColCount();

	var startRow = 5;
	var startCol = 6;

	for (var i = startRow; i < rowcount; i++) {
		for (var j = startCol; j < colcount; j++) {
			var cell = tableComp.getCell(i, j);
			replaceAsImg(tableComp, cell);
		}
	}
}

function replaceAsImg(tableComp, cell) {
	// println("cell:" + cell.getId());
	var textComp = getTextResult(cell);
	if (textComp == null) {
		// println("no text");
		return;
	}
	var value = textComp.value;
	if (!com.succez.commons.util.StringUtils.isEmpty(value)) {
		// println("set text style");
		setStyle(tableComp, cell, textComp);
		return;
	}
	// println("replace");
	var tid = textComp.getId();
	var imgid = tid + "_img";

	cell.addComponent("image", imgid);
	cell.remove(tid);

	var img = cell.getComponent(imgid);
	setImageOptions(img);
}

function getTextResult(cell) {
	var comps = cell.getSubRealComponents();
	var count = comps == null ? 0 : comps.size();
	if (count == 0) {
		return;
	}
	for (var i = 0; i < count; i++) {
		var comp = comps.get(i);
		var cid = comp.getId();
		if (com.succez.commons.util.StringUtils.startsWith(cid, "text1_")) {
			return comp;
		}
	}
	return null;
}

function setStyle(table, cell, text) {
	var row = cell.getRow();
	var col = cell.getCol();
	var preCell = table.getCell(row, col - 2);
	var value = com.succez.commons.util.StringUtils.obj2str(preCell.getValue());
	println("cell:" + cell.getId());
	println("precell:" + value);
	if (com.succez.commons.util.StringUtils.equals(value, "2.0")) {
		println("style:2");
		setTextBackgroundStyle(text, "#D5FF8E");
	}
	else if (com.succez.commons.util.StringUtils.equals(value, "3.0")) {
		println("style:3");
		setTextBackgroundStyle(text, "#ABE1ED");
	}
}

function setTextBackgroundStyle(text, bg) {
	text.addStyle("background:" + bg);
}

function setImageOptions(img) {
	img.setOption("size", "fixed");
	img.setOption("top", "0");
	img.setOption("left", "0");
	img.setOption("position", "absolute");
	img.setOption("width", "100%");
	img.setOption("height", "100%");
	img.setOption("src", "/meta/DCAP/collections/publish/RCGK/M/TNFGL/MBYWQD1/forms/default/images/bias.png");
}