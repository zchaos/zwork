package jar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.collections.map.HashedMap;

public class JarReader {
	public static void main(String[] args) throws IOException {
		String path = "/succez/test/source";
		File dir = new File(path);

		Map<String, List<String>> map = new HashMap<String, List<String>>();
		File[] fs = dir.listFiles();
		int len = fs == null ? 0 : fs.length;
		for (int i = 0; i < len; i++) {
			File file = fs[i];
			if (!file.getAbsolutePath().endsWith(".jar")) {
				continue;
			}

			List<String> list = new ArrayList<String>(1000);
			map.put(file.getAbsolutePath(), list);
			readJar(file, list);
		}

		String key = "bookmark";

		Iterator<Entry<String, List<String>>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, List<String>> entry = it.next();
			String file = entry.getKey();
			List<String> list = entry.getValue();

			System.out.println(file);
			int size = list.size();
			for (int i = 0; i < size; i++) {
				String p = list.get(i);
				if (p.indexOf(key) >= 0) {
					System.out.println("\t\t" + p);
				}
			}
		}
	}

	public static void readJar(File file, List<String> list) throws IOException {
		JarFile jar = new JarFile(file);
		try {
			Enumeration<JarEntry> e = jar.entries();
			while (e.hasMoreElements()) {
				JarEntry entry = e.nextElement();
				String name = entry.getName();
				list.add(name);
			}
		}
		finally {
			jar.close();
		}
	}
}
