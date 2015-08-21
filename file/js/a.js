function onAfterReportCalc(args) {
	var result = args.result;
	var imgComp = result.getComponent("image");
	imgComp.setOption("class","custom-class");
}
