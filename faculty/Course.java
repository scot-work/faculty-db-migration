package faculty;

import java.util.*;

class Course {
    Faculty faculty;
    int id;
    boolean active;
    String title;
    String name;
    String location;
    String dayAndTime;
    String description;
    String supplementalUrl;
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
     * Get the path for this course
     * @return Path for the course
     */
    String path() {
        return faculty.handle + "/courses/" + this.name;
    }


    /**
     * Output this course as an XML (pcf) file
     */
    /*void output(){
		XmlHelper.outputBasicFile(faculty, title, getContentAsHtml(), path(), active);
	}*/

    /**
     * Return an html string with the course content
     * @return
     */
    String getContentAsHtml() {
        String content = "<h2>" + title + "</h2>";
        content += "<img src=\"" + url() +  name + ".jpg\" alt=\"" + photoDescription + "\"/>";

        // time
        content += ("<p><strong>Time:</strong> " + dayAndTime + " </p>");
        // location
        content += ("<p><strong>Location:</strong> " + location + " </p>");
        // Supplemental URL
        content += ("<p><strong>Supplemental URL:</strong> <a href=\"" + supplementalUrl + "\">" + supplementalUrl + "</a></p>");
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
    /*public String toStringx() {
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
	}*/


    /**
     * Get the URL for this course page
     * @return URL
     */
    String url() {
        return "/people/" + faculty.handle + "/courses/" + name;
    }

}
