package compare;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.succez.commons.util.io.IOUtils;

/**
 * 判断两个文件内容是否相等
 * <p>Copyright: Copyright (c) 2013<p>
 * <p>succez<p>
 * @author zhuchx
 * @createdate 2013-5-21
 */
public class FileCompare {
	private static final int BUFFER_SIZE = 1024 * 128;

	/**
	 * 比较两个文件的内容是否相等，如果相等，返回true
	 * @param file1
	 * @param fiel2
	 * @return
	 * @throws IOException 
	 */
	public static boolean compareFile(File file1, File file2) throws IOException {
		boolean exist1 = exist(file1);
		boolean exist2 = exist(file2);
		if (exist1 != exist2) {
			return false;
		}
		if (!exist1) {
			return true;
		}
		boolean isFile1 = isFile(file1);
		boolean isFile2 = isFile(file2);
		if (!isFile1 || !isFile2) {
			return false;
		}

		int len = BUFFER_SIZE;
		byte[] bs1 = new byte[len];
		byte[] bs2 = new byte[len];

		FileInputStream in1 = new FileInputStream(file1);
		try {
			FileInputStream in2 = new FileInputStream(file2);
			try {
				while (true) {
					int len1 = IOUtils.read(in1, bs1, len);
					int len2 = IOUtils.read(in2, bs2, len);
					if (len1 != len2) {
						return false;
					}
					if (len1 == 0) {
						return true;
					}
					if (!compareBytes(bs1, bs2)) {
						return false;
					}
				}
			}
			finally {
				in2.close();
			}
		}
		finally {
			in1.close();
		}
	}

	/**
	 * 比较两个字节数组是否相等
	 * @return
	 */
	public static boolean compareBytes(byte[] bs1, byte[] bs2) {
		int len1 = bs1 == null ? 0 : bs1.length;
		int len2 = bs2 == null ? 0 : bs2.length;
		if (len1 != len2) {
			return false;
		}
		for (int i = 0; i < len1; i++) {
			if (bs1[i] != bs2[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断文件是否存在，如果存在，返回true
	 * @param file
	 * @return
	 */
	public static boolean exist(File file) {
		return file != null && file.exists();
	}

	/**
	 * 判断是否文件
	 * @param file
	 * @return
	 */
	public static boolean isFile(File file) {
		return file != null && file.exists() && file.isFile();
	}

	/**
	 * 判断是否目录
	 * @param file
	 * @return
	 */
	public static boolean isDirectory(File file) {
		return file != null && file.exists() && file.isDirectory();
	}
}
