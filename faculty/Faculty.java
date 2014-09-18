package faculty;

import faculty.Education;
import faculty.Degree;

import java.io.IOException;
import java.util.*;

class Faculty {
    String lastName;
    String firstName;
    String handle;
    int facultyID;
    int towerID;
    boolean isActive;
    List<String> emails;
    Education education;
    String bio;
    String officeHours;
    String phone;
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

    // module settings

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
     * Convert to HTML string
     * @return
     */
    String getContentAsHtml(){
        String content = "";	

        content += ("\n<h2>" + fullName() + "</h2>"); 
        if (photoSetting == 2){
            String photoURL = Migrate.baseURL + handle + "/" + handle + ".jpg";
            content += ("<img src=\"" + photoURL + "\" alt=\"" + photoDescription + "\" />");
        }
        content += ("\n<p>");
        for(Position p : positions){
            content += (p.toHTML() + "<br />");
        }
        if (Migrate.isValid(titles)){
            content += ("\n<em>" + titles + "</em><br />");
        }
        content += ("</p>");
        content += ("<h4>Email</h4>");
        for(String email : emails){
            content += ("<p><a href=\"mailto:" + email + "\">" + email + "</a></p>");
        }
        content += ("<h4>Phone Number(s)</h4>");
        content += ("<p>" + phone + "</p>");
        if (Migrate.isValid(officeHours)){
            content += ("<h4>Office Hours</h4>");
            content += ("<p>" + officeHours + "</p>");
        }
        // Courses
        content += (coursesActive?"":"<div style=\"background-color: yellow;\">");
        content += ("<hr /><h3>Courses</h3>");
        content += ("<ul>");
        String courseNav = "";
        for(Course c : courses) {
            if(c.active){
                courseNav += ("\n<li><a href=\"" + c.url() + "\">" + c.title + "</a></li>");
            }
        }
        XmlHelper.outputSidenav(this.handle + "/courses/", courseNav);
        content += courseNav;
        content += ("</ul>");
        content += (coursesActive?"":"</div>");

        // Education
        content += (educationActive?"":"<div style=\"background-color: yellow;\">");
        content += ("<hr /><h3>Education</h3>");
        content += ("<ul>");  
        for (Degree d : education.degrees) {
            content += ("<li>" + d + "</li>");
        }
        content += ("</ul>");
        content += (educationActive?"":"</div>");

        // Licenses and Certificates
        content += (licensesCertificatesActive?"":"<div style=\"background-color: yellow;\">");
        content += ("<hr /><h3>Licenses &amp; Certificates</h3>");
        content += ("<ul>");
        for (License l : licenses){
            content += ("<li>" + l + "</li>");
        }
        content += ("</ul>");
        content += (licensesCertificatesActive?"":"</div>");

        // Bio
        if (Migrate.isValid(bio)){
            content += (bioActive?"":"<div style=\"background-color: yellow;\">");
            content += ("<hr /><h3>Bio</h3>");
            content += (bio);
            content += (bioActive?"":"</div>");
        }
        content += ("<hr /><h3>Links</h3>");
        content += ("<ul>");
        for (Link l : links ){
            content += ("<li><a href=\"" + l.url + "\">" + l.label + "</a></li>");
        }
        content += ("</ul>");
        return content;
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
