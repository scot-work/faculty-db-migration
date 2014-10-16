package faculty;

import faculty.Education;

import java.io.IOException;
import java.util.*;

class Faculty {
    String lastName;
    String firstName;
    //String handle;
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

    boolean active; // inactive = valid account that has chosen to be hidden
    boolean bioActive;
    boolean isValid; // invalid = not found in PeopleSoft tables
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
    String fullName() {
        String result = firstName;
        if (Migrate.isValid(middleName)){
            result += " " + middleName;
        }
        result += " " + lastName;
        return result;
    }

    String jobTitles() {
        String result = "";
        for (Position p : this.positions){
            result += p.toHTML();
        }
        if (Migrate.isValid(titles)){
            result += titles;
        }
        return result;
    }
    
    /**
    * Return the handle (everything up to the @ in the email)
    */
    String handle() {
        String handle = "invalid";
        if (Migrate.isValid(sjsuEmail)) {
           handle = sjsuEmail.substring(0, sjsuEmail.lastIndexOf('@'));
        } 
        return handle;
    }

    /**
     * Return formatted official phone number
     * @return
     */
    String phone() {
        if (Migrate.isValid(this.phone) && this.phone.length() == 9){
    	String result = "(";
    	result += this.phone.substring(0, 3);
    	result += ") ";
    	result += this.phone.substring(3, 6);
    	result += "-";
    	result += this.phone.substring(6, 10);
    	return result;
    } else {
        return this.phone;
    }
    }

    /**
    * Output list of links
    */
    String links() {
        String result = "";
        if (links.size() > 0) {
            result += "<ul>";
            for (Link l : links){
                result += l.toHTML();
            }
            result += "</ul>";
        }
        return result;
    }

    /**
     * Output faculty information as a series of XML (.pcf) files
     */
    void output() {
        if (!isValid) {
            return;
        }

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
        XmlHelper.outputBasicFile(this, fullName() + " Publications", publicationContent, "/" + this.handle() + "/publications/", publicationsActive);

        // Output empty sidenav
        XmlHelper.outputSidenav("/" + this.handle() + "/publications/", "");

        // Output research page
        String researchContent = "";
        researchContent += ("<h2>" + fullName() + "</h2>"); 
        researchContent += ("\n<h3>Research &amp; Scholarly Activity</h3>");
        researchContent += ("\n<ul>");
        for (Research r : research) {
            researchContent += (r.getContentAsHtml());
        }
        researchContent += ("</ul>");
        XmlHelper.outputBasicFile(this, fullName() + "Research", researchContent, "/" + this.handle() + "/research/", researchActive);

        // Output empty sidenav
            XmlHelper.outputSidenav("/" + this.handle() + "/research/" , "");

        // Output custom pages
        String customContent = "";
        for (CustomPage cp : customPages) {
            customContent = cp.getContentAsHtml();
            XmlHelper.outputBasicFile(this, cp.name, customContent, "/" + this.handle() + "/" + cp.name, true);

            // Output empty sidenav
            XmlHelper.outputSidenav("/" + this.handle() + "/" + cp.name, "");
        }

        // save photo
        if (photoSetting == 2) {
            try {
                Migrate.saveDocument(Migrate.liveSiteBaseDir + "/people/" + handle() + "/" + handle() + ".jpg", 
                        Migrate.outputDirectory + "/people/" + this.handle() + "/" + handle() + ".jpg");
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        // write sidenav.inc	
        String sidenav = "";
        sidenav += coursesActive?"\n<li><a href=\"" + Migrate.baseURL + "/" + this.handle() + "/" +  "courses/\">Courses" + "</a></li>":"";
        sidenav += publicationsActive?"\n<li><a href=\"" + Migrate.baseURL + "/" + this.handle() + "/" +  "publications/\">Publications &amp; Presentations" 
                + "</a></li>":"";
        sidenav += researchActive?"\n<li><a href=\"" + Migrate.baseURL + "/" + this.handle() + "/" +  "research/\">Research &amp; Scholarly Activity" 
                + "</a></li>":"";
        sidenav += professionalServicesActive?"\n<li><a href=\"" + Migrate.baseURL + "/" + this.handle() + "/" +  "professional_service/\">Professional &amp; Service Activity" 
                + "</a></li>":"";
        XmlHelper.outputSidenav("/" + this.handle(), sidenav);

        // Create list of course links
        if (courses.size() > 0) {
            String courseList = "";
            for (Course c : courses) {
                if (c.active) {
                    courseList += "\n<li><a href=\"/people/" + c.path() + "\">" + c.title + "</a></li>";
                } else {
                    System.out.println("Course not active: " + c.title);
                }
            }
            XmlHelper.outputBasicFile(this, "Courses", "<ul>" + courseList + "</ul>", "/" + this.handle() + "/courses", true );

            // Output sidenav.inc
            XmlHelper.outputSidenav("/" + this.handle() + "/courses", courseList);
        }

        for (Course c : courses) {
            // Course object has its own output method
            //c.output();
            XmlHelper.outputBasicFile(c.faculty, c.title, c.getContentAsHtml(), c.path(), c.active);

            // Output empty sidenav
            XmlHelper.outputSidenav(c.path(), "");
        }

        // Copy primarynav.pcf file to faculty directory
        XmlHelper.outputPcf("/" + this.handle(), StringConstants.PRIMARYNAV, "primarynav.pcf");
    }

    /**
     * Get Photo URL for this faculty if there is one
     * @return
     */
    public String photoUrl() {
        if (this.photoSetting == 2) {
            return "/people/" + handle() + "/" + handle() + ".jpg";
        } else {
            return null;
        }
    }
}
