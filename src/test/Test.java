package test;

import com.succez.commons.util.StringUtils;

public class Test {
	public static void main(String[] args) {
		String str = " a  b   c ";
		String[] rs = StringUtils.split(str, ' ');
		System.out.println(rs);
	}

}
