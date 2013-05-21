package css;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.succez.commons.util.StringUtils;
import com.succez.commons.util.io.IOUtils;

/**
 * 结果表显示时，因为ie中css有不能超过32个的限制，所以部分css用@import导入的方法引用。
 * 在查问题时，这些通过@import引用的css不好调试，此类将这些@import的css转换为实际的css引用
 * <p>Copyright: Copyright (c) 2013<p>
 * <p>succez<p>
 * @author zhuchx
 * @createdate 2013-4-20
 */
public class CssGet {
	private static final String IMPORT = "@import ";

	private static final String PREFIX = "../";

	public static void main(String[] args) throws Exception {
		CssGet cg = new CssGet();
		String[] href = new String[] {
				"http://localhost:8080/succezbi/static-files-aggcss/v_0/sz.bi.api.aggregation/skin/default/sz-bi-api-all-import.css",//
				""//
		};
		String links = cg.htmlLinkCss(href);
		System.out.println(links);
	}

	/**
	 * 将http://localhost:8080/succezbi/static-files-aggcss/system/skin/default/system-all-import.css?_t_=1366278575246转换为以下内容：
	 * <link rel="stylesheet" href="http://localhost:8080/succezbi/static-file/sz.commons.button/skin/default/button.css?_t_=1366278575246" type="text/css" media="screen" />
	 * <link rel="stylesheet" href="http://localhost:8080/succezbi/static-file/sz.commons.checkbox/skin/default/checkbox.css?_t_=1366278575246" type="text/css" media="screen" />
	 * @param links
	 * @return
	 * @throws Exception
	 */
	public String htmlLinkCss(String... links) throws Exception {
		int len = links == null ? 0 : links.length;

		List<String> css = new ArrayList<String>();
		for (int i = 0; i < len; i++) {
			List<String> list = getLinkCssFiles(links[i]);
			if (list == null || list.size() == 0) {
				continue;
			}
			css.addAll(list);
		}

		int size = css.size();
		StringBuilder build = new StringBuilder(size * 100);
		for (int i = 0; i < size; i++) {
			build.append(buildLink(css.get(i))).append("\r\n");
		}
		return build.toString();
	}

	/**
	 * 传入href，生成完成的<link>
	 * 如href=http://localhost:8080/succezbi/static-file/sz.commons.button/skin/default/button.css?_t_=1366278575246
	 * 返回结果为：<link rel="stylesheet" href="http://localhost:8080/succezbi/static-file/sz.commons.button/skin/default/button.css?_t_=1366278575246" type="text/css" media="screen" />
	 * @param href
	 * @return
	 */
	private String buildLink(String href) {
		StringBuilder build = new StringBuilder(100);
		build.append("<link");
		//href
		build.append(" href=\"");
		build.append(href);
		build.append("\"");

		build.append(" rel=\"stylesheet\"");
		build.append(" type=\"text/css\"");
		build.append(" media=\"screen\"");
		build.append(" />");
		return build.toString();
	}

	/**
	 * css中可以通过import导入其它的css，此方法将import的css转换成实际的css。
	 * 如http://localhost:8080/succezbi/static-files-aggcss/system/skin/default/system-all-import.css的内容为：
	 * @import "../../../../static-file/sz.commons.button/skin/default/button.css?_t_=1366278575246";
	 * @import "../../../../static-file/sz.commons.checkbox/skin/default/checkbox.css?_t_=1366278575246";
	 * 
	 * 则此方法返回
	 * @param links
	 * @return
	 * @throws Exception 
	 */
	public List<String> getLinkCssFiles(String link) throws Exception {
		if (StringUtils.isEmpty(link)) {
			return Collections.emptyList();
		}
		String content = getContent(link);
		if (StringUtils.isEmpty(content)) {
			return Collections.emptyList();
		}
		int index = content.indexOf(IMPORT);
		if (index == -1) {
			return Collections.emptyList();
		}
		String cssFileStr = content.substring(index);
		String[] importCssFiles = StringUtils.splitByWholeSeparator(cssFileStr, "\n");
		int len = importCssFiles == null ? 0 : importCssFiles.length;
		if (len == 0) {
			return Collections.emptyList();
		}
		String https = getHttps(link);
		List<String> list = new ArrayList<String>(len);
		for (int i = 0; i < len; i++) {
			String importCssFile = importCssFiles[i].trim();
			if (StringUtils.isEmpty(importCssFile)) {
				continue;
			}
			String cssUrl = getCssFileUrl(importCssFile, https);
			list.add(cssUrl);
		}
		return list;
	}

	/**
	 * 将@import "../../../../static-file/sz.commons.button/skin/default/button.css?_t_=1366278575246";转换为实际的路径
	 * @param importCssFile
	 * @param https
	 * @return
	 */
	private String getCssFileUrl(String importCssFile, String https) {
		String path = importCssFile.substring(IMPORT.length() + 1, importCssFile.length() - 2);//importCssFile.length() - 2这里减2是去掉末尾的";
		while (path.startsWith(PREFIX)) {
			path = path.substring(PREFIX.length());
		}
		return https + "/" + path;
	}

	/**
	 * 获得指定url的内容
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public String getContent(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		InputStream in = url.openStream();
		try {
			return IOUtils.toString(in, "UTF-8");
		}
		finally {
			in.close();
		}
	}

	private String getHttps(String url) throws Exception {
		int start = url.indexOf("//");
		if (start == -1) {
			return url;
		}
		int index = url.indexOf('/', start + 2);
		if (index == -1) {
			return url;
		}
		String https = url.substring(0, index);
		if (canConnect(https)) {
			return https;
		}
		int nextIndex = url.indexOf('/', index + 1);
		if (nextIndex == -1) {
			return url;
		}
		return url.substring(0, nextIndex);

	}

	private boolean canConnect(String urlStr) throws Exception {
		URL url = new URL(urlStr);
		try {
			InputStream in = url.openStream();
			try {
				IOUtils.toString(in, "UTF-8");
			}
			finally {
				in.close();
			}
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}
}
