package faculty;

import java.util.*;

class Section {
String description;
String name;
List<Document> docs; 
List<Link> links;
String url;
int id;
int position;
boolean active;

 Section(int id){
	// System.out.println("\ncreating section: " + Integer.toString(id));
	this.id = id;
}

 public String toString() {

	String result = "\n";
	if (Migrate.isValid(name)){
		result += name + " ";
	}
	if (Migrate.isValid(description)){
		result += description;
	}
	if (docs.size() > 0) {
		result += "\nDocuments: ";
		for (Document d : docs){
			//result += "\n" + d.label + ", " + d.url;
			result += "\n" + d.toHTML();
		}
	}
	if (links.size() > 0) {
		result += "\nLinks: ";
		for (Link l : links){
			//result += "\n" + l.label + ", " + l.url;
			result += "\n" + l.toHTML();
		}
	}
	return result;
}

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
		for (Document d : docs){
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