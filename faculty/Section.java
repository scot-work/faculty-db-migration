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

public Section(int id){
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

}