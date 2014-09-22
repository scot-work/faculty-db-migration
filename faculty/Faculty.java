package faculty;

import faculty.Education;

import java.io.IOException;
import java.util.*;

class Faculty {
    String lastName;
    String firstName;
    String handle;
    int facultyID;
    int towerID;
    boolean isActive;
    // List<String> emails;
    String sjsuEmail;
    String alternateEmail;
    Boolean alternateEmailPreferred;
    Education education;
    String bio;
    String officeHours;
    String phone;
    String alternatePhone;
    Boolean alternatePhonePreferred;
    String additionalInfo;
    String photoDescription;
    String middleName;
    int photoSetting;
    String titles;
    List<License> licenses;
    List<Position> positions;
    List<Course> courses;
    List<Link> links;
    List<CustomPage> customPages;
    boolean useFormEntryForPublications;
    List<Publication> publications;
    List<Research> research;
    String publicationsText;

    boolean active; // inactive faculty will not be in sjsu_people_details_master.towerid
    boolean bioActive;
    boolean coursesActive;
    boolean educationActive;
    boolean licensesCertificatesActive;
    boolean linksActive;
    boolean professionalServicesActive;
    boolean publicationsActive;
    boolean researchActive;
    boolean expertActive;

    /**
     * Create a new Faculty object
     * @param facultyID internal id number
     */
    Faculty(int facultyID) {
        this.facultyID = facultyID;
    }

    /**
     * Full name
     * @return Full name of the faculty 
     */
    String fullName(){
        String result = firstName;
        if (Migrate.isValid(middleName)){
            result += " " + middleName;
        }
        result += " " + lastName;
        return result;
    }
    
    /**
     * Return formatted official phone number
     * @return
     */
    String phone(){
    	String result = "(";
    	result += this.phone.substring(0, 3);
    	result += ") ";
    	result += this.phone.substring(3, 6);
    	result += "-";
    	result += this.phone.substring(6, 10);
    	return result;
    }

    /**
     * Output faculty information as a series of XML (.pcf) files
     */
    void output() {
        XmlHelper.outputProfilePage(this);
        
        // Output publications page
        String publicationContent = "";
        if ((useFormEntryForPublications && (publications.size() > 0)) 
                || Migrate.isValid(publicationsText)) {
            publicationContent = ("<h2>" + fullName() + "</h2>"); 
            publicationContent += ("<h3>Publications &amp; Presentations</h3>");
            publicationContent += ("<ul>");
            if (useFormEntryForPublications && (publications.size() > 0)) {
                for (Publication p : publications) {
                    publicationContent += (p.getContentAsHtml());
                }
            } else {
                publicationContent += ("<li>" + publicationsText + "</li>");
            }
            publicationContent += ("</ul>");
        }
        XmlHelper.outputBasicFile(this, fullName() + " Publications", publicationContent, this.handle + "/publications/", publicationsActive);

        // Output research page
        String researchContent = "";
        researchContent += ("<h2>" + fullName() + "</h2>"); 
        researchContent += ("\n<h3>Research &amp; Scholarly Activity</h3>");
        researchContent += ("\n<ul>");
        for (Research r : research) {
            researchContent += (r.getContentAsHtml());
        }
        researchContent += ("</ul>");
        XmlHelper.outputBasicFile(this, fullName() + "Research", researchContent, this.handle + "/research/", researchActive);

        // Output custom pages
        String customContent = "";
        for (CustomPage p : customPages){
            customContent = p.getContentAsHtml();
            XmlHelper.outputBasicFile(this, p.name, customContent, this.handle + "/" + p.name, true);
        }

        // save photo
        if (photoSetting == 2){
            try {
                Migrate.saveImage(Migrate.liveSiteBaseDir + handle + "/" + handle + ".jpg", 
                        Migrate.outputDirectory + handle + "/" + handle + ".jpg");
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        // write sidenav.inc	
        String sidenav = "";
        sidenav += coursesActive?"\n<li><a href=\"" + Migrate.baseURL + this.handle + "/" +  "courses/\">Courses" + "</a></li>":"";
        sidenav += publicationsActive?"\n<li><a href=\"" + Migrate.baseURL + this.handle + "/" +  "publications/\">Publications &amp; Presentations" 
                + "</a></li>":"";
        sidenav += researchActive?"\n<li><a href=\"" + Migrate.baseURL + this.handle + "/" +  "research/\">Research &amp; Scholarly Activity" 
                + "</a></li>":"";
        sidenav += professionalServicesActive?"\n<li><a href=\"" + Migrate.baseURL + this.handle + "/" +  "professional_service/\">Professional &amp; Service Activity" 
                + "</a></li>":"";
        XmlHelper.outputSidenav(this.handle, sidenav);

        // courses
        if (courses.size() > 0) {
            // Output course page
            String courseContent = "\n<ul>";
            for (Course c : courses) {
                courseContent += "\n<li><a href=/people/" + c.path() + "\">" + c.title + "</a></li>";
            }
            courseContent += "</ul>";
            XmlHelper.outputBasicFile(this, "Courses", courseContent, this.handle + "/courses", true );
        }

        for (Course c : courses) {
            // Course object has its own output method
            //c.output();
            XmlHelper.outputBasicFile(c.faculty, c.title, c.getContentAsHtml(), c.path(), c.active);
        }
    }

    /**
     * Get Photo URL for this faculty if there is one
     * @return
     */
    public String photoUrl() {
        if (this.photoSetting == 2){
            return Migrate.outputDirectory + handle + "/" + handle + ".jpg";
        } else {
            return null;
        }
    }
}
