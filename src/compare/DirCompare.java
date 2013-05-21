package compare;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 比较目录中的结构和所有后代的内容是否相等，包含区别列表
 * <p>Copyright: Copyright (c) 2013<p>
 * <p>succez<p>
 * @author zhuchx
 * @createdate 2013-5-21
 */
public class DirCompare {
	/**
	 * 主目录 比 比较目录 少的文件或目录
	 */
	private List<File> lessFile = new ArrayList<File>();

	/**
	 * 主目录 比 比较目录 多的文件或目录
	 */
	private List<File> moreFile = new ArrayList<File>();

	/**
	 * 主目录 与 比较目录 中的文件类型不同，如主目录中的文件为“文件”，而比较目录中的文件为“目录”
	 * diffMainFile中保存主目录中文件类型不同的文件
	 * diffCompareFile保存比较目录中文件类型不同的文件
	 */
	private List<File> diffMainType = new ArrayList<File>();

	private List<File> diffCompareType = new ArrayList<File>();

	/**
	 * 主目录 与 比较目录 中的文件内容不同
	 */
	private List<File> diffMainContent = new ArrayList<File>();

	private List<File> diffCompareContent = new ArrayList<File>();

	/**
	 * 主目录
	 */
	private File mainDir;

	/**
	 * 比较目录
	 */
	private File compareDir;

	private boolean debug;

	public DirCompare(File mainDir, File compareDir) {
		this.mainDir = mainDir;
		this.compareDir = compareDir;
		this.debug = true;
	}

	public void compare() throws IOException {
		compareResult(mainDir, compareDir);
	}

	/**
	 * @param mf 主目录中的文件
	 * @param cf 比较目录中的文件
	 * @throws IOException 
	 */
	private void compareResult(File mf, File cf) throws IOException {
		boolean exist1 = exist(mf);
		boolean exist2 = exist(cf);
		if (!exist1) {
			if (exist2) {
				lessFile.add(cf);
			}
		}
		else {
			if (!exist2) {
				moreFile.add(mf);
			}
			else {
				compareExist(mf, cf);
			}
		}
	}

	/**
	 * 比较两个都存在的文件或目录
	 * @param mf
	 * @param cf
	 * @throws IOException
	 */
	private void compareExist(File mf, File cf) throws IOException {
		if (debug) {
			System.out.println(mf.getAbsolutePath());
		}
		boolean isFile1 = isFile(mf);
		boolean isFile2 = isFile(cf);
		if (isFile1 != isFile2) {
			diffMainType.add(mf);
			diffCompareType.add(cf);
		}
		else {
			if (isFile1) {//两个都是文件
				if (!FileCompare.compareFile(mf, cf)) {
					diffMainContent.add(mf);
					diffCompareContent.add(cf);
				}
			}
			else {//两个都是目录
				compareExistDirectory(mf, cf);
			}
		}
	}

	/**
	 * 比较两个存在的目录
	 * @param mf
	 * @param cf
	 * @throws IOException
	 */
	private void compareExistDirectory(File mf, File cf) throws IOException {
		File[] mlist = mf.listFiles();
		File[] clist = cf.listFiles();

		int mlen = mlist == null ? 0 : mlist.length;
		HashMap<String, File> mmap = new HashMap<String, File>(mlen);
		for (int i = 0; i < mlen; i++) {
			File file = mlist[i];
			mmap.put(file.getName(), file);
		}

		int clen = clist == null ? 0 : clist.length;
		for (int i = 0; i < clen; i++) {
			File file2 = clist[i];
			File file1 = mmap.get(file2.getName());
			if (file1 == null) {
				lessFile.add(file2);
			}
			else {
				compareExist(file1, file2);
				mmap.remove(file2.getName());
			}
		}

		moreFile.addAll(mmap.values());
	}

	private boolean exist(File file) {
		return FileCompare.exist(file);
	}

	private boolean isFile(File file) {
		return FileCompare.isFile(file);
	}

	public void printResult(Writer w) throws IOException {
		w.write("\r\n");
		{
			int lessFileSize = lessFile.size();
			w.write("缺少的文件：共" + lessFileSize);
			w.write("\r\n");
			for (int i = 0; i < lessFileSize; i++) {
				w.write("  ");
				w.write(lessFile.get(i).getAbsolutePath());
				w.write("\r\n");
			}
		}
		{

			int moreFileSize = moreFile.size();
			w.write("增加的文件：共" + moreFileSize);
			w.write("\r\n");
			for (int i = 0; i < moreFileSize; i++) {
				w.write("  ");
				w.write(moreFile.get(i).getAbsolutePath());
				w.write("\r\n");
			}
		}
		{

			int diffTypeSize = diffMainType.size();
			w.write("类型不同的文件：共" + diffTypeSize);
			w.write("\r\n");
			for (int i = 0; i < diffTypeSize; i++) {
				w.write("  ");
				w.write(diffMainType.get(i).getAbsolutePath());
				w.write("\r\n");
				w.write("  ");
				w.write(diffCompareType.get(i).getAbsolutePath());
				w.write("\r\n");
			}
		}
		{

			int diffContentSize = diffMainContent.size();
			w.write("内容不同的文件：共" + diffContentSize);
			w.write("\r\n");
			for (int i = 0; i < diffContentSize; i++) {
				w.write("  ");
				w.write(diffMainContent.get(i).getAbsolutePath());
				w.write("\r\n");
				w.write("  ");
				w.write(diffCompareContent.get(i).getAbsolutePath());
				w.write("\r\n");
			}
		}
	}
}
