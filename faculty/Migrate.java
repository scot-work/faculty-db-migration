package faculty;

import java.sql.*;
import java.util.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import faculty.Degree;
import faculty.Faculty;
import faculty.Education;
import faculty.Queries;
import faculty.Position;

public class Migrate {
// Get all live pages
  public static String ConnectionString = "jdbc:mysql://zara.sjsu.edu:3030/people?user=migration&password=m1gr@t3";
  // public static String baseURL = "http://dev.sjsu.edu/people/";
  public static String baseURL = "/people/";
  // public static String outputFile = "faculty.out";
  public static String outputDirectory = "/var/www/html/people/";
  // live sites
  public static String liveSiteBaseDir = "http://dev.sjsu.edu/people/";

  public static void main(String[] args) {

    /*
     * Get a database connection
     */
    // MySQL drivers
    try {
      Class.forName("com.mysql.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    try {
      Connection conn = DriverManager.getConnection(ConnectionString);
      // List<Faculty> facultyList = getFacultyList(conn);
      getFacultyList(conn);
      // processFacultyHome(conn, facultyList);
      // printAllFaculty(facultyList);
      conn.close();
    } catch (SQLException ex) {
      System.out.println("SQLException: " + ex.getMessage());
      System.out.println("SQLState: " + ex.getSQLState());
      System.out.println("VendorError: " + ex.getErrorCode());
    }
  }

/**
*  Create a list of published faculty
*
*/
static void getFacultyList(Connection conn) throws java.sql.SQLException {
  List<Faculty> facultyList = new ArrayList();
  PreparedStatement stmt = conn.prepareStatement(Queries.PublishedQuery);
  ResultSet rs = stmt.executeQuery();
  while (rs.next()) {
    // Create a new Faculty object with basic information
    Faculty faculty = new Faculty(rs.getInt("faculty_id"));
    faculty.lastName = rs.getString("last_name");
    faculty.firstName = rs.getString("first_name");
    faculty.handle = rs.getString("handle");
    processFacultyHome(conn, faculty);
    faculty.outputHTML();    
  }
  rs.close();
  stmt.close();
}

static void processFacultyHome(Connection conn, Faculty f) throws java.sql.SQLException {

    // Get SJSU ID
  PreparedStatement stmt = conn.prepareStatement(Queries.TowerIDQuery);
  stmt.setInt(1, f.facultyID);
  ResultSet rs = stmt.executeQuery();
  int id = 0;
  while (rs.next()){
    id = rs.getInt("towerid");
  }
  rs.close();
  stmt.close();

  f.towerID = id;

    // Get official info
  stmt = conn.prepareStatement(Queries.OfficialInfoQuery);
  stmt.setInt(1, f.towerID);
  rs = stmt.executeQuery();
  while(rs.next()) {
    List emails = new ArrayList<String>();
    emails.add(rs.getString("email_addr"));
    f.emails = emails;
    f.firstName = rs.getString("first_name");
    f.middleName = rs.getString("middle_name");
    f.lastName = rs.getString("last_name");
    f.phone = rs.getString("phone");
  }
  rs.close();
  stmt.close();

    // Get self-entered info
  stmt = conn.prepareStatement(Queries.DetailsQuery);
  stmt.setInt(1, f.facultyID);
  rs = stmt.executeQuery();
  while(rs.next()) {
    f.additionalInfo = rs.getString("additional_info");
    f.officeHours = rs.getString("office_hours");
    f.bio = rs.getString("bio");
    f.photoSetting = rs.getInt("photo_setting");
    f.photoDescription = rs.getString("photo_description");
    if (isValid(rs.getString("phone"))){
      f.phone = rs.getString("phone");
    }
    if (isValid(rs.getString("first_name"))){
      f.firstName = rs.getString("first_name");
    }
    if (isValid(rs.getString("middle_name"))){
      f.middleName = rs.getString("middle_name");
    }
    if (isValid(rs.getString("email"))){
      f.emails.add(rs.getString("email"));
    }
    f.titles = rs.getString("titles");
    f.handle = rs.getString("published_handle");
  }
  rs.close();
  stmt.close();
  f.education = new Education(f.towerID, f.facultyID, conn);

  // Download image
  if (f.photoSetting == 2){
    try {
      saveImage(liveSiteBaseDir + f.handle + "/" + f.handle + ".jpg", outputDirectory + f.handle + "/" + f.handle + ".jpg");
    } catch (IOException e){
      e.printStackTrace();
    }
  }

    // Get licenses and Certificates
  List licenses = new ArrayList<License>();
  stmt = conn.prepareStatement(Queries.LicenseQuery);
  stmt.setInt(1, f.facultyID);
  rs = stmt.executeQuery();
  while(rs.next()) {
    License l = new License();
    l.licenseName = rs.getString("license_name");
    l.year = rs.getInt("year");
    l.issuedBy = rs.getString("issued_by");
    l.descr = rs.getString("descr");
    l.countryCode = rs.getString("country_code");
    l.stateCode = rs.getString("state_code");
    l.stateName = rs.getString("state_name");   
    licenses.add(l);
  }
  rs.close();
  stmt.close();
  f.licenses = licenses;

    // Clean up licenses
  for (License l : f.licenses){
    stmt = conn.prepareStatement(Queries.GetCountryFromCode);
    stmt.setString(1, l.countryCode);
    rs = stmt.executeQuery();
    while(rs.next()) {
      l.country = rs.getString("descr");
    }
    rs.close();
    stmt.close();

    stmt = conn.prepareStatement(Queries.GetStateFromCode);
    stmt.setString(1, l.countryCode);
    stmt.setString(2, l.stateCode);
    rs = stmt.executeQuery();
    while(rs.next()) {
      l.state = rs.getString("descr");
    }
    rs.close();
    stmt.close();
  }

    // Get job title(s)
  stmt = conn.prepareStatement(Queries.JobsQuery);
  stmt.setInt(1, f.towerID);
  rs = stmt.executeQuery();
  List positions = new ArrayList<Position>();
  Position position = null;
  while(rs.next()) {
    position = new Position(rs.getString("position_descr"));
    position.department = rs.getString("deptid_descr");
    positions.add(position);
  }
  rs.close();
  stmt.close();
  f.positions = positions;

    // Get home page links
  List homeLinks = new ArrayList<Link>();
  stmt = conn.prepareStatement(Queries.HomeLinksQuery);
  stmt.setInt(1, f.facultyID);
  rs = stmt.executeQuery();
  Link homeLink = null;
  while(rs.next()) {
    homeLink = new Link(rs.getString("label"), rs.getString("url"));
    homeLinks.add(homeLink);
  }
  f.links = homeLinks;
  rs.close();
  stmt.close();

    // Get Courses
  List courses = new ArrayList<Course>();
  Course course = null;
  stmt = conn.prepareStatement(Queries.CoursesQuery);
  stmt.setInt(1, f.facultyID);
  rs = stmt.executeQuery();
  while(rs.next()) {
    course = new Course(rs.getString("title"));
    course.facultyHandle = f.handle;
    course.name = rs.getString("name");
    course.location = rs.getString("location");
    course.dayAndTime = rs.getString("time");
    course.id = rs.getInt("id");
    course.description = rs.getString("description");
    course.url = rs.getString("url");
    courses.add(course);
  }
  rs.close();
  stmt.close();
  f.courses = courses;

    // process courses
    // get sections
  Section section = null;
  for (Course c : f.courses) {
    List sections = new ArrayList<Section>();
    stmt = conn.prepareStatement(Queries.CourseSectionsQuery);
    stmt.setInt(1, c.id);
    rs = stmt.executeQuery();
    while(rs.next()) {
      section = new Section(rs.getInt("id"));
      section.name = rs.getString("name");
      section.description = rs.getString("description");
        // example: /people/scot.close/courses/test/s0/52-Fall-09-1 Greensheet.pdf
      section.url = c.url() + "/s" + rs.getString("position");
      sections.add(section);
    }
    c.sections = sections;
    rs.close();
    stmt.close();
      // get docs for section
      // SELECT * FROM sjsu_people_course_section_docs WHERE course_section_id=?

    for (Section s : c.sections) {
      List documents = new ArrayList<Document>();
      stmt = conn.prepareStatement(Queries.SectionDocsQuery);
      stmt.setInt(1, s.id);
      rs = stmt.executeQuery();
      while(rs.next()) {
        Document d = new Document(rs.getString("label"), s.url + "/" + rs.getString("path").substring(rs.getString("path").lastIndexOf('/') + 1));
        documents.add(d);
      }
      s.docs = documents;
      rs.close();
      stmt.close();

      List sectionLinks = new ArrayList<Link>();
      stmt = conn.prepareStatement(Queries.SectionLinksQuery);
      stmt.setInt(1, s.id);
      rs = stmt.executeQuery();
      while(rs.next()) {
        Link l = new Link(rs.getString("label"), rs.getString("url"));
        sectionLinks.add(l);
      }
      s.links = sectionLinks;
      rs.close();
      stmt.close();
    } 
    } // end of for course
  }
/*liveSiteBaseDir
* Utilities
*/
static boolean isValid(String s) {
  if (s == null || s.equals("null")) {
    return false;
  } else if (s.length() < 1) {
    return false;
  } 
  return true;
}

public static void saveImage(String imageUrl, String destinationFile) throws IOException {
  URL url = new URL(imageUrl);
  InputStream is = url.openStream();
  OutputStream os = new FileOutputStream(destinationFile);

  byte[] b = new byte[2048];
  int length;

  while ((length = is.read(b)) != -1) {
    os.write(b, 0, length);
  }

  is.close();
  os.close();
}

static void printAllFaculty(List<Faculty> facultyList) {
 for (Faculty f : facultyList) {
  f.outputHTML();
}
}


}


