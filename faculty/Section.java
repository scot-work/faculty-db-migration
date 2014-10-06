package faculty;

import java.util.*;

class Section {
	String description;
	String name;
	List<Doc> docs; 
	List<Link> links;
	String url;
	int id;
	int position;
	boolean active;

 Section(int id){
	// System.out.println("\ncreating section: " + Integer.toString(id));
	this.id = id;
}

/**
* Return an HTML chunk for displaying this section
* 
* @return HTML string
*/
 String toHTML() {
		String result = "\n";
		//if (!active){result += "<div style=\"background-color: yellow;\">";}
	result += active?"":"<div style=\"background-color: yellow;\">";
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
	result += active?"":"</div>";
	return result;
}

}