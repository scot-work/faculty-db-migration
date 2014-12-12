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
    String photoExtension;
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
        return StringConstants.SITEROOT + faculty.handle() + "/courses/" + this.name;
    }

    String photoURL() {
        return path() + "/" + name + photoExtension;
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
        // String content = "<h2>" + title + "</h2>";
        String content = "";
        // time
        if (Migrate.isValid(dayAndTime)) {
           content += ("<p><strong>Time:</strong> " + dayAndTime + " </p>");
        }
        // location
        if (Migrate.isValid(location)) {
            content += ("<p><strong>Location:</strong> " + location + " </p>");
        }   
        // Supplemental URL
        if (Migrate.isValid(supplementalUrl)) {
            content += ("<p><strong>Supplemental URL:</strong> <a href=\"" + supplementalUrl + "\">" + supplementalUrl + "</a></p>");
        }
        if (Migrate.isValid(description)) {
            content += ("<h2>Description</h2>");
            content += ("<p>" + description + "</p>");
        }
        for (Section s : sections){
            content += (s.toHTML());
        }
        return content;
    }


    /**
     * Get the URL for this course page
     * @return URL
     */
    String url() {
        return StringConstants.SITEROOT + faculty.handle() + "/courses/" + name;
    }

}
