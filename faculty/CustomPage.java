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

	CustomPage(int id) {
		this.id = id;
	}

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