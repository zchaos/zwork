package build;

import java.io.File;
import java.io.IOException;

import com.succez.commons.util.io.FileUtils;

public class BuildCreator {
	public static void main(String[] args) throws IOException {
		String[][] addresses = new String[][] {// 
		{ "jira", "http://update.atlassian.com/atlassian-eclipse-plugin/e3.8" },//
				{ "jetty", "http://run-jetty-run.googlecode.com/svn/trunk/updatesite" },//
				{ "jshint", "http://github.eclipsesource.com/jshint-eclipse/updates/" },//
				{ "pmd", "http://pmd.sf.net/eclipse" },//
				{ "findbugs", "http://findbugs.cs.umd.edu/eclipse" },//
		};

		String target = "/ztmp/eclipse/build.xml";
		String pluginsDir = "/ztmp/eclipse/plugins/";

		StringBuilder builder = new StringBuilder(1000);
		builder.append("<project name=\"project\" default=\"download_eclipse_plugins\">");
		builder.append("<target name=\"download_eclipse_plugins\">");

		int len1 = addresses.length;
		for (int i = 0; i < len1; i++) {
			String[] address = addresses[i];
			String name = address[0];
			String url = address[1];

			builder.append("<property name=\"" + name + "\" value=\"" + pluginsDir + name + "\" />");
			builder.append("<p2.mirror destination=\"file:/${" + name + "}\" description=\"" + name
					+ "\" verbose=\"true\">");
			builder.append("<source>");
			builder.append("<repository name=\"" + name + "\" location=\"" + url + "\" />");
			builder.append("</source>");
			builder.append("<slicingOptions includeFeatures=\"true\" followStrict=\"true\" latestVersionOnly=\"true\" />");
			builder.append("</p2.mirror>");
		}

		builder.append("</target>");
		builder.append("</project>");

		FileUtils.writeStringToFile(new File(target), builder.toString(), "UTF-8");
	}
}
