package faculty;

import java.util.*;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

class Course {
	Faculty faculty;
	int id;
	boolean active;
	String title;
	String name;
	String location;
	String dayAndTime;
	String description;
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
	 * Output as a pcf page
	 */
	void writePcf(){
		String content = getContentAsHtml();
		String path = faculty.handle + "/courses/" + this.name;
		XmlHelper.toXml(faculty, content, path);
	}
	

	/**
	 * Return an html string with the course content
	 * @return
	 */
	private String getContentAsHtml() {
		String content = "<h2>" + title + "</h2>";
		content += "<img src=\"" + url +  name + ".jpg\" alt=\"" + photoDescription + "\"/>";
		if (active) {
			content += ("<em>active</em>");
		} else {
			content += ("<em>inactive/hidden</em>");
		}
		// time
		content += ("<p><strong>Time:</strong> " + dayAndTime + " </p>");
		// location
		content += ("<p><strong>Location:</strong> " + location + " </p>");
		// Supplemental URL
		content += ("<p><strong>Supplemental URL:</strong> <a href=\"" + url + "\">" + url + "</a></p>");
		content += ("<h2>Description</h2>");
		content += ("<p>" + description + "</p>");
		for (Section s : sections){
			content += (s.toHTML());
		}
		return content;
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
	 * Get the URL for this course page
	 * @return URL
	 */
	String url() {
		return "/people/" + faculty.handle + "/courses/" + name;
	}

}
