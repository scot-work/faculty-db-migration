package faculty;

import java.util.*;
import java.io.PrintWriter;

class Course {
	int id;
	String title;
	String name;
	String location;
	String dayAndTime;
	String description;
	String facultyHandle;
	String url;
	List<Section> sections; 

	public Course(String title) {
		this.title = title;
	}

	public String toString() {
		String result = title + ", " + name + ", " + location + ", " + dayAndTime;
		if (description != null){
			result += "\n" + description;
		}
		if (sections != null) {
			result += "\nSections:";
			for (Section s : sections) {
				result += s;
			}
		}
		return result;
	}

	public void toHTML(PrintWriter writer) {
		writer.println(HtmlStrings.HEADER);
    writer.println(HtmlStrings.TITLE);
    writer.println(HtmlStrings.BODY);
    writer.println("<h2>" + title + "</h2>");
    // time
    writer.println("<p><strong>Time:</strong> " + dayAndTime + " </p>");
    // location
    writer.println("<p><strong>Location:</strong> " + location + " </p>");
    // Supplemental URL
    writer.println("<p><strong>Supplemental URL:</strong> " + url + " </p>");

    writer.println(HtmlStrings.FOOTER);
	}

	public String url() {
		return "/people/" + facultyHandle + "/courses/" + name;
	}

}
