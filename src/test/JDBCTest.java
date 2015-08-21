package test;

import java.util.List;

import jdbc.JDBCUtils;

public class JDBCTest {
	public static void main(String[] args) throws Exception {
		String confName = "exam";
		String tableName = "ZIDHHF_ZIDONGHHF";
		String[] fields = { "TEXTAREA1" };
		String[][] condition = null;
		List<List<Object>> datas = JDBCUtils.getData(confName, tableName, fields, condition);
		String value = (String) datas.get(0).get(0);
		int len = value.length();
		for (int i = 0; i < len; i++) {
			char c = value.charAt(i);
			System.out.println(c + "  " + ((int) c));
		}
	}
}
