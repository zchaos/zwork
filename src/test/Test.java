package test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import compare.DirCompare;

public class Test {

	public static void main(String[] args) throws IOException {
		String dir1 = "/ztmp/swt4.3/swt";
		String dir2 = "/ztmp/swt4.3/swt-debug";

		DirCompare compare = new DirCompare(new File(dir1), new File(dir2));
		compare.compare();

		StringWriter w = new StringWriter(1000);
		compare.printResult(w);
		System.out.println(w.toString());
	}
}
