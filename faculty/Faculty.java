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
 String email;
 Education education;
   // inactive faculty will not be in sjsu_people_details_master.towerid
 boolean active;
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
// photo
// links

 Faculty(int facultyID){
  this.facultyID = facultyID;
}

/**
* Output one faculty
*/
public String toString() {
  String result = "\nName: " + firstName;
  if (Migrate.isValid(middleName)) {
    result += " " + middleName;
  }
  result += " " + lastName; 
  result += "\nHandle: ";
  result += handle;
  result += "\nEmail(s): " + email;
  result += "\nJob Title(s): ";
  for(Position p : positions){
    result += "\n" + p.positionDescription + ", " + p.department;
  }
  result += "\nOther Titles: " + titles;
  result += "\nBio: " + bio;
  result += "\nPhone: " + phone;
  result += "\nOffice Hours: " + officeHours;
  result += "\nEducation: ";
  for (Degree d : education.degrees) {
    result += d;
  }
  result += "\nAdditional Information: " + additionalInfo;
  if (licenses.size() > 0){
    result += "\nLicenses:";
    for (License l : licenses){
      result += "\n" + l;
    }
  }
  result += "\n\nCourses:";
  for(Course c : courses) {
    result += "\n" + c;
  }
  return result;
}

public void outputHTML() {
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
    writer.println("<p>");
    for(Position p : positions){
      writer.println("<em>" + p.positionDescription + ", " + p.department + "</em><br />");
    }
    writer.println("<em>" + titles + "</em><br />");
    writer.println("<h4>Email</h4>");
    writer.println("<p>" + email + "</p>");
    writer.println("<h4>Phone Number(s)</h4>");
    writer.println("<p>" + phone + "</p>");
    writer.println("<h4>Office Hours</h4>");
    writer.println("<p>" + officeHours + "</p>");
    writer.println("</p>");

    writer.println("<hr /><h3>Courses</h3>");
    writer.println("<ul>");
    for(Course c : courses) {
      writer.print("<li><a href=\"" + c.url() + "\">" + c.title + "</a></li>");
    }
    writer.println("</ul>");

    writer.println("<hr /><h3>Education</h3>");
    writer.println("<ul>");  
    for (Degree d : education.degrees) {
      writer.print("<li>" + d + "</li>");
    }
    writer.println("</ul>");

    writer.println("<hr /><h3>Licenses &amp; Certificates</h3>");
    writer.println("<ul>");
    for (License l : licenses){
      writer.print("<li>" + l + "</li>");
    }
    writer.println("</ul>");

    writer.println("<hr /><h3>Bio</h3>");
    writer.println(bio);

    writer.println("<hr /><h3>Links</h3>");


    writer.println(HtmlStrings.FOOTER);
    writer.close();
  } catch (FileNotFoundException | UnsupportedEncodingException e){
    e.printStackTrace();
  }
}

}