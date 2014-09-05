package faculty;

import java.util.*;
import java.io.PrintWriter;

class CustomPage {
	int id;
	String title;
	String name;
	String content;
	String url;
	List<Link> links;
	List<Doc> documents;
	boolean active;

	/**
	 * Create a new custom page
	 * @param id
	 */
	CustomPage(int id) {
		this.id = id;
	}

	/**
	 * Return page contents as html
	 * @return
	 */
	String getContentAsHtml() {
		String result = "\n<h2>" + title + "</h2>";
		result += "\n<p>" + content + "</p>";
		if (documents.size() > 0) {
			result += "\n<h3>Documents</h3>";
			result += "\n<ul>";
			for (Doc d : documents){
				result += d.toHTML();
			}
			result += "\n</ul>";
		}
		return result;
	}

	/**
	 * Output Custom Page as a web page
	 * @param writer
	 */
	void toHTML(PrintWriter writer) {
		writer.println(HtmlStrings.HEADER);
		writer.println(HtmlStrings.TITLE);
		writer.println(HtmlStrings.BODY);
		writer.println("<h2>" + title + "</h2>");
		if (active) {
			writer.println("<p><em>inactive</em></p>");
		}
		writer.println(HtmlStrings.FOOTER);
	}
}