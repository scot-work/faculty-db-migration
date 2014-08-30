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
	List<Document> documents;
	boolean active;

	/**
	 * Create a new custom page
	 * @param id
	 */
	CustomPage(int id) {
		this.id = id;
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