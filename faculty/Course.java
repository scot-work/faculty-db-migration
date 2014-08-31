package faculty;

import java.util.*;
import java.io.PrintWriter;

class Course {
	int id;
	boolean active;
	String title;
	String name;
	String location;
	String dayAndTime;
	String description;
	String facultyHandle;
	String url;
	String photoDescription;
	int photoSetting;
	List<Section> sections; 

	/**
	 * Create a new Course
	 * @param title Course Title
	 */
	Course(String title) {
		this.title = title;
	}

	/**
	 * Output course content as a formatted string
	 */
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

	/**
	 * Output course as a web page
	 * @param writer
	 */
	void toHTML(PrintWriter writer) {
		writer.println(HtmlStrings.HEADER);
		writer.println(HtmlStrings.TITLE);
		writer.println(HtmlStrings.BODY);
		if (photoSetting != 0){
			writer.println("<img src=\"" + url +  name + ".jpg\" alt=\"" + photoDescription + "\"/>");
		}
		writer.println("<h2>" + title + "</h2>");
		if (active) {
			writer.println("<em>active</em>");
		} else {
			writer.println("<em>inactive/hidden</em>");
		}
		// time
		writer.println("<p><strong>Time:</strong> " + dayAndTime + " </p>");
		// location
		writer.println("<p><strong>Location:</strong> " + location + " </p>");
		// Supplemental URL
		writer.println("<p><strong>Supplemental URL:</strong> <a href=\"" + url + "\">" + url + "</a></p>");
		writer.println("<h2>Description</h2>");
		writer.println("<p>" + description + "</p>");
		for (Section s : sections){
			writer.println(s.toHTML());
		}
		writer.println(HtmlStrings.FOOTER);
	}

	/**
	 * Get the URL for this course page
	 * @return URL
	 */
	String url() {
		return "/people/" + facultyHandle + "/courses/" + name;
	}

}
