package faculty;

import java.util.*;

class Section {
	String description;
	String name;
	List<Doc> docs; 
	List<Link> links;
	// String url;
	int id;
	int position;
	boolean active;

 Section(int id){
	// System.out.println("\ncreating section: " + Integer.toString(id));
	this.id = id;
}

/**
*   This is a hack that may or may not work
*	I don't understand why most (but not all) document URLs use /s? where ? = section position - 1
*   I can't just subtract 1 because sometimes position is 0
*/
String url() {
	String result = "/s";
	if (position > 0) {
		result += String.valueOf(position - 1);
	} else {
		result += "0";
	}
	return result;
}

/**
* Return an HTML chunk for displaying this section
* 
* @return HTML string
*/
 String toHTML() {
	String result = "\n";
	// Don't output an empty heading
	if (Migrate.isValid(description) || docs.size() > 0 || links.size() > 0) {
		if (Migrate.isValid(name)){
			result += "<h2>" + name + "</h2>";
		}
		if (Migrate.isValid(description)){
			result += "<p>" + description + "</p>";
		}
		if (docs.size() > 0) {
			result += "<h3>Documents</h3><ul>";
			for (Doc d : docs){
				//result += "\n" + d.label + ", " + d.url;
				result += d.toHTML();
			}
			result += "</ul>";
		}
		if (links.size() > 0) {
			result += "<h3>Links</h3><ul>";
			for (Link l : links){
				//result += "\n" + l.label + ", " + l.url;
				result += l.toHTML();
			}
			result += "</ul>";
		}
}
	return result;
}

}