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
    List<ProfessionalActivity> professionalActivities;
    String publicationsText;

    boolean active; // inactive = valid account that has chosen to be hidden
    boolean bioActive;
    boolean isValid; // invalid = not found in PeopleSoft tables
    boolean coursesActive;
    boolean educationActive;
    boolean licensesCertificatesActive;
    boolean linksActive;
    boolean professionalActivityActive;
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
        String result = lastName;
        result += ", " + firstName;
        if (Migrate.isValid(middleName)){
            result += " " + middleName;
        }
        
        return result;
    }

    String additionalInfo() {
        String result = "";
        if (Migrate.isValid(additionalInfo)) {
            // is this html or plain text?
            if (additionalInfo.indexOf("<") == -1) {
                result = "<p>" + additionalInfo + "</p>";
            } else {
                result = additionalInfo;
            }
        }
        return result;
    }

    String licenses() {
        String result = "";
        if (this.licenses.size() > 0) {
            result = "<ul>";
            for (License l : this.licenses) {
                result += l.toHTML();
            }
            result += "</ul>";
        }
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
           // remove apostrophe
           handle = handle.replace("'", "");
        } 
        return "/" + handle;
    }

    /**
     * Return formatted official phone number
     * @return
     */
    String phone() {
        if (Migrate.isValid(this.phone) && this.phone.length() == 10) {
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

        // Output home page
        XmlHelper.outputProfilePage(this);

        // Output publications page
        String publicationContent = "";
        if ((useFormEntryForPublications && (publications.size() > 0)) 
                || Migrate.isValid(publicationsText)) {
            publicationContent = ("<h2>" + fullName() + "</h2>"); 
            publicationContent += ("<h3>Publications &amp; Presentations</h3>");
           
            if (useFormEntryForPublications && (publications.size() > 0)) {
                publicationContent += ("<ul>");
                for (Publication p : publications) {
                    publicationContent += p.getContentAsHtml();
                }
                publicationContent += ("</ul>");
            } else {
                publicationContent += (publicationsText);
            }
            
        }
        XmlHelper.outputBasicFile(this, "Publications & Presentations", publicationContent, StringConstants.SITEROOT + this.handle() + "/publications/", publicationsActive, "20");

        // Output empty sidenav
        XmlHelper.outputSidenav(StringConstants.SITEROOT + this.handle() + "/publications/", "");

        // Output research page
        String researchContent = "";
        researchContent += ("<h2>" + fullName() + "</h2>"); 
        researchContent += ("\n<h3>Research &amp; Scholarly Activity</h3>");
        researchContent += ("\n<ul>");
        for (Research r : research) {
            researchContent += (r.getContentAsHtml());
        }
        researchContent += ("</ul>");
        XmlHelper.outputBasicFile(this, "Research & Scholarly Activity", researchContent, StringConstants.SITEROOT + this.handle() + "/research/", researchActive, "30");
        // Output empty sidenav
        XmlHelper.outputSidenav(StringConstants.SITEROOT + this.handle() + "/research/" , "");

        // Output professional and service activities page
        String professionalContent = "";
        professionalContent += ("<h2>" + fullName() + "</h2>"); 
        professionalContent += ("\n<h3>Professional &amp; Service Activity</h3>");
        professionalContent += ("\n<ul>");
        for (ProfessionalActivity pa : professionalActivities) {
            professionalContent += pa.toHTML();
        }
        professionalContent += ("</ul>");
        XmlHelper.outputBasicFile(this, "Professional & Service Activity", professionalContent, StringConstants.SITEROOT + this.handle() + "/professional_service/", professionalActivityActive, "40");
        // Output empty sidenav
        XmlHelper.outputSidenav(StringConstants.SITEROOT + this.handle() + "/professional_service/" , "");
        

        // Output custom pages
        String customContent = "";
        for (CustomPage cp : customPages) {
            customContent = cp.getContentAsHtml();
            XmlHelper.outputBasicFile(this, cp.name, customContent, StringConstants.SITEROOT + this.handle() + "/" + cp.name, true);

            // Output empty sidenav
            XmlHelper.outputSidenav(StringConstants.SITEROOT + this.handle() + "/" + cp.name, "");
        }

        // save photo
        if (photoSetting == 2) {
            try {
                Migrate.saveDocument(Migrate.liveSiteBaseDir + StringConstants.SITEROOT + handle() + handle() + ".jpg", 
                        Migrate.outputDirectory + StringConstants.SITEROOT + handle() + handle() + ".jpg");
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        // write sidenav.inc	
        String sidenav = "";
        sidenav += coursesActive?"\n<li><a href=\"" + StringConstants.SITEROOT + this.handle() + "/" +  "courses/\">Courses" + "</a></li>":"";
        sidenav += publicationsActive?"\n<li><a href=\"" + StringConstants.SITEROOT + this.handle() + "/" +  "publications/\">Publications &amp; Presentations" 
                + "</a></li>":"";
        sidenav += researchActive?"\n<li><a href=\"" + StringConstants.SITEROOT + this.handle() + "/" +  "research/\">Research &amp; Scholarly Activity" 
                + "</a></li>":"";
        sidenav += professionalActivityActive?"\n<li><a href=\"" + StringConstants.SITEROOT + this.handle() + "/" +  "professional_service/\">Professional &amp; Service Activity" 
                + "</a></li>":"";
        XmlHelper.outputSidenav(StringConstants.SITEROOT + this.handle(), sidenav);

        // Create list of course links
        if (courses.size() > 0) {
            String courseList = "";
            for (Course c : courses) {
                if (c.active) {
                    courseList += "\n<li><a href=\"" + c.path() + "\">" + c.title + "</a></li>";
                } else {
                    System.out.println("Course not active: " + c.title);
                }
            }
            XmlHelper.outputBasicFile(this, "Courses", "<ul>" + courseList + "</ul>", StringConstants.SITEROOT + this.handle() + "/courses", true, "10" );

            // Output sidenav.inc
            XmlHelper.outputSidenav(StringConstants.SITEROOT + this.handle() + "/courses", courseList);
        }

        for (Course c : courses) {
            if (c.photoSetting == 2) {
                XmlHelper.outputBasicFile(c.faculty, c.title, c.getContentAsHtml(), c.path(), c.active, "10", c.photoURL(), c.photoDescription);
            } else {
                XmlHelper.outputBasicFile(c.faculty, c.title, c.getContentAsHtml(), c.path(), c.active, "10", "", "");
            }

            // Output empty sidenav
            XmlHelper.outputSidenav(c.path(), "");
        }

        // Copy primarynav.pcf file to faculty directory
        XmlHelper.outputPcf(StringConstants.SITEROOT + this.handle(), StringConstants.PRIMARYNAV, "primarynav.pcf");
    }

    /**
     * Get Photo URL for this faculty if there is one
     * @return
     */
    public String photoUrl() {
        if (this.photoSetting == 2) {
            return StringConstants.SITEROOT + this.handle() + handle() + ".jpg";
        } else {
            return null;
        }
    }
}
