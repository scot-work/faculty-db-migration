package faculty;

import java.util.*;

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
		String result = "";
		result += "\n<p>" + content + "</p>";
		if (documents.size() > 0) {
			result += "\n<h3>Documents</h3>";
			result += "\n<ul>";
			for (Doc d : documents){
				result += d.toHTML();
			}
			result += "\n</ul>";
		}
		if (links.size() > 0) {
			result += "\n<h3>Links</h3>";
			result += "\n<ul>";
			for (Link l : links){
				result += l.toHTML();
			}
			result += "\n</ul>";
		}
		return result;
	}

}