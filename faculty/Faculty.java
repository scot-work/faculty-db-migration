package faculty;

import faculty.Education;
import faculty.Degree;
import java.util.*;
import java.io.*;
import java.io.PrintWriter;
/**
 * select spu.towerid from sjsu_people_users spu, sjsu_people_details spd where spu.faculty_id = spd.id
 * 
 */ 
class Faculty {
 String lastName;
 String firstName;
 String handle;
 int facultyID;
 int towerID;
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
// licenses
 List<License> licenses;
 List<Position> positions;
 List<Course> courses;
 List<Link> links;
 boolean useFormEntryForPublications;
 List<Publication> publications;
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

 Faculty(int facultyID) {
  this.facultyID = facultyID;
}

 void outputHTML() {
  try {
    String outputDir = Migrate.outputDirectory + this.handle;
    new File(outputDir).mkdirs();
    String outputFile = outputDir + "/index.html";
    PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
    writer.println(HtmlStrings.HEADER);
    writer.println(HtmlStrings.TITLE);
    writer.println(HtmlStrings.BODY);

    writer.println("<h2>" + firstName);
    if (Migrate.isValid(middleName)) {
      writer.println(" " + middleName);
    }
    writer.println(" " + lastName + "</h2>"); 
    if (photoSetting == 2){
      String photoURL = Migrate.baseURL + handle + "/" + handle + ".jpg";
      writer.println("<img src=\"" + photoURL + "\" alt=\"" + photoDescription + "\" />");
    }
    writer.println("<p>");
    for(Position p : positions){
      writer.println("<em>" + p.positionDescription + ", " + p.department + "</em><br />");
    }
    writer.println("<em>" + titles + "</em><br />");
    writer.println("</p>");
    writer.println("<h4>Email</h4>");
    for(String email : emails){
      writer.println("<p><a href=\"mailto:" + email + "\">" + email + "</a></p>");
    }
    writer.println("<h4>Phone Number(s)</h4>");
    writer.println("<p>" + phone + "</p>");
    writer.println("<h4>Office Hours</h4>");
    writer.println("<p>" + officeHours + "</p>");
    writer.println("</p>");

    // Courses
    writer.println(coursesActive?"":"<div style=\"background-color: yellow;\">");
    writer.println("<hr /><h3>Courses</h3>");
    writer.println("<ul>");
    for(Course c : courses) {
      if(c.active){
      writer.print("<li><a href=\"" + c.url() + "\">" + c.title + "</a></li>");
    }
    }
    writer.println("</ul>");
    writer.println(coursesActive?"":"</div>");

    // Education
    writer.println(educationActive?"":"<div style=\"background-color: yellow;\">");
    writer.println("<hr /><h3>Education</h3>");
    writer.println("<ul>");  
    for (Degree d : education.degrees) {
      writer.print("<li>" + d + "</li>");
    }
    writer.println("</ul>");
    writer.println(educationActive?"":"</div>");

    // Licenses and Certificates
    writer.println(licensesCertificatesActive?"":"<div style=\"background-color: yellow;\">");
    writer.println("<hr /><h3>Licenses &amp; Certificates</h3>");
    writer.println("<ul>");
    for (License l : licenses){
      writer.print("<li>" + l + "</li>");
    }
    writer.println("</ul>");
    writer.println(licensesCertificatesActive?"":"</div>");

    // Bio
    writer.println(bioActive?"":"<div style=\"background-color: yellow;\">");
    writer.println("<hr /><h3>Bio</h3>");
    writer.println(bio);
    writer.println(bioActive?"":"</div>");
    writer.println("<hr /><h3>Links</h3>");
    writer.println("<ul>");
    for (Link l : links ){
      writer.println("<li><a href=\"" + l.url + "\">" + l.label + "</a></li>");
    }
    writer.println("</ul>");
    writer.println(HtmlStrings.FOOTER);
    writer.close();

  } catch (FileNotFoundException | UnsupportedEncodingException e){
    e.printStackTrace();
  }

// Output course pages
  for (Course c : courses) {
    try {
     String outputDir = Migrate.outputDirectory + this.handle + "/courses/" + c.name;
     String outputFile = outputDir + "/index.html";
     new File(outputDir).mkdirs();
     PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
     c.toHTML(writer);
     writer.close();
   } catch (FileNotFoundException | UnsupportedEncodingException e) {
    e.printStackTrace();
  }
}

  // Output publications
if ((useFormEntryForPublications && (publications.size() > 0)) || Migrate.isValid(publicationsText)) {
  try {
   String outputDir = Migrate.outputDirectory + this.handle + "/publications/";
   String outputFile = outputDir + "/index.html";
   new File(outputDir).mkdirs();
   PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
   writer.println(HtmlStrings.HEADER);
    writer.println(HtmlStrings.TITLE);
    writer.println(HtmlStrings.BODY);
    writer.println("<h2>" + firstName);
    if (Migrate.isValid(middleName)) {
      writer.println(" " + middleName);
    }
    writer.println(" " + lastName + "</h2>"); 
    writer.println("<h3>Publications &amp; Presentations</h3>");
   writer.println("<ul>");
   if (useFormEntryForPublications && (publications.size() > 0)) {
     for (Publication p : publications) {
       writer.println(p.toHTML());
      }
    } else {
      writer.println("<li>" + publicationsText + "</li>");
    }
    writer.println("</ul>");
    writer.println(HtmlStrings.FOOTER);
    writer.close();
 } catch (FileNotFoundException | UnsupportedEncodingException e) {
  e.printStackTrace();
}

} 


}
}
