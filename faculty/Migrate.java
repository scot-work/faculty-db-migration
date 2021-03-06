package faculty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 
 * @author Scot Close
 *
 */
public class Migrate {

    public static String outputDirectory;
    public static String liveSiteBaseDir;
    public static String localDocRoot;
    // Set to true to process faculty without outputting any files
    static Boolean suppressFileOutput = false;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        // Choose live.properties or dev.properties  
        
        // InputStream stream = loader.getResourceAsStream("dev.properties");
        InputStream stream = loader.getResourceAsStream("live.properties");
        
        try {
            prop.load(stream);
        } catch (IOException e ) {
            e.printStackTrace();
        }
        Migrate.outputDirectory = prop.getProperty("outputDirectory");
        Migrate.liveSiteBaseDir = prop.getProperty("liveSiteBaseDir");
        Migrate.localDocRoot = prop.getProperty("localDocRoot");
        if (args.length == 0) {
            try {
                processAllFacultyPages(prop);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            if (args[0].equals("empty")) {
                try {
                    outputEmptyFolders(prop);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else if (args[0].equals("experts")){
                try {
                    System.out.println("Outputting Experts Data");
                    outputExpertsData(prop);
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // Output outline files for testing
            } else if  (args[0].equals("pcf")) {
                outputEmptyPcfs();
            } else {
                try {
                    processIndividualFaculty(prop, args[0]);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
    * Process one faculty based on email address
    */
    private static void processIndividualFaculty(Properties prop, String name) throws java.sql.SQLException {
        System.out.println("Processing single faculty: " + name);
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(prop.getProperty("ConnectionString"));
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        PreparedStatement stmt = conn.prepareStatement(Queries.GetUserByEmail);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();
        Faculty faculty = null;
        while (rs.next()) {
            faculty = new Faculty(rs.getInt("faculty_id"));
            faculty.firstName = rs.getString("first_name");
        }
        rs.close();
        stmt.close();
        if (faculty == null) {
            System.out.println("Faculty " + name + " not found.");

        } else {
            processFacultySite(conn, faculty);
            faculty.output(); 
        }
        conn.close();
    }

    /**
    * Output PCF files without any content (for testing only)
    */
    private static void outputEmptyPcfs() {
        Document doc = XmlHelper.getProfileOutline();
        String xml = XmlHelper.getStringFromDoc(doc);
        XmlHelper.outputPcf("people", xml);
    }

    /**
     * Output experts data as XML file for use later
     */
    private static void outputExpertsData(Properties prop) throws java.sql.SQLException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(prop.getProperty("ConnectionString"));
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        PreparedStatement stmt = conn.prepareStatement(Queries.PublishedQuery);
        ResultSet rs = stmt.executeQuery();
        String handle = "";

        int facultyID;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();


            // root elements
            Document doc = docBuilder.newDocument();
            doc.setXmlStandalone(true);
            Element root = doc.createElement("experts");
            doc.appendChild(root);
            while (rs.next()) {
                handle = rs.getString("handle");
                Expert expert = new Expert(handle);
                facultyID = rs.getInt("faculty_id");
                PreparedStatement expertsQuery = conn.prepareStatement(Queries.GetExpertsData);
                expertsQuery.setInt(1, facultyID);
                ResultSet experts = expertsQuery.executeQuery();
                while (experts.next()){
                    expert.handle = handle;
                    expert.categoryOne = experts.getString("expertise_category_1");
                    expert.categoryTwo = experts.getString("expertise_category_2");
                    expert.contactForSpeaking = experts.getInt("contact_for_speaking") == 1;
                    expert.contactForResearch = experts.getInt("contact_for_research") == 1;
                    expert.contactByMedia = experts.getInt("contact_by_media") == 1;
                    expert.summary = experts.getString("expertise_summary");
                }
                root.appendChild(expert.createXml(doc));
            }
            System.out.println(XmlHelper.getStringFromDoc(doc));
        } catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }
        rs.close();
        stmt.close();
        conn.close();

    }

    /**
     *  Output an empty folder for each faculty so permissions can be set in OU Campus
     *
     */
    static void outputEmptyFolders(Properties prop) throws java.sql.SQLException {
        Connection conn = null;
        String outputDirectory = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(prop.getProperty("ConnectionString"));
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        PreparedStatement stmt = conn.prepareStatement(Queries.PublishedQuery);
        ResultSet rs = stmt.executeQuery();
        String handle = "";
        while (rs.next()) {
            handle = rs.getString("handle");
            String outputDir = outputDirectory + handle;
            new File(outputDir).mkdirs();
        }
        rs.close();
        stmt.close();
        conn.close();
    }


    /**
     * Get faculty one at a time from database and export
     * @param prop properties file
     * @throws java.sql.SQLException
     */
    static void processAllFacultyPages(Properties prop) throws java.sql.SQLException {
        // Get database connection
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            conn = DriverManager.getConnection(prop.getProperty("ConnectionString"));
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }

        // Get list of all published faculty sites
        // PreparedStatement stmt = conn.prepareStatement(Queries.PublishedQuery);
        PreparedStatement stmt = conn.prepareStatement(Queries.FacultyListQuery);
        // SELECT id, first_name, website_live FROM sjsu_people_details
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            // Create a new Faculty object and output pages
            Faculty faculty = new Faculty(rs.getInt("id"));
            faculty.firstName = rs.getString("first_name");
            // faculty.isActive = (rs.getInt("website_live") == 1);
            processFacultySite(conn, faculty);
            faculty.output(); 
        }
        rs.close();
        stmt.close();
        conn.close();
    }

    /**
     * Process one faculty site
     * @param conn
     * @param currentFaculty
     * @throws java.sql.SQLException
     */
    static void processFacultySite(Connection conn, Faculty currentFaculty) throws java.sql.SQLException {
        // Get SJSU ID from Faculty ID
        PreparedStatement stmt = conn.prepareStatement(Queries.TowerIDQuery);
        // SELECT towerid FROM sjsu_people_users WHERE faculty_id=?";
        stmt.setInt(1, currentFaculty.facultyID);
        ResultSet rs = stmt.executeQuery();
        int id = 0;
        // Need to check for demo users without valid ID
        while (rs.next()) {
            try {
                id = rs.getInt("towerid");
            } catch (java.sql.SQLException e) {
                // invalid account, skip
                return;
            }
        }
        rs.close();
        stmt.close();
        currentFaculty.towerID = id;

        // Get official info
        stmt = conn.prepareStatement(Queries.OfficialInfoQuery);
        // SELECT * FROM sjsu_people_details_master WHERE towerid=?
        stmt.setInt(1, currentFaculty.towerID);
        rs = stmt.executeQuery();
        // NOTE: if faculty not found, faculty is not active
        currentFaculty.isValid = false;
        while(rs.next()) {
            currentFaculty.isValid = true;
            currentFaculty.sjsuEmail = rs.getString("email_addr");
            currentFaculty.firstName = rs.getString("first_name");
            currentFaculty.middleName = rs.getString("middle_name");
            currentFaculty.lastName = rs.getString("last_name");
            currentFaculty.phone = rs.getString("phone");
        }
        rs.close();
        stmt.close();
        
        System.out.println("\nStarting " + currentFaculty.fullName());

        // Self-entered information
        stmt = conn.prepareStatement(Queries.DetailsQuery);
        // SELECT * FROM sjsu_people_details WHERE id=?
        stmt.setInt(1, currentFaculty.facultyID);
        rs = stmt.executeQuery();
        while(rs.next()) {
            currentFaculty.isActive = (rs.getInt("website_live") == 1);
            currentFaculty.additionalInfo = rs.getString("additional_info");
            currentFaculty.officeHours = rs.getString("office_hours");
            currentFaculty.bio = rs.getString("bio");
            currentFaculty.photoSetting = rs.getInt("photo_setting");
            currentFaculty.photoDescription = rs.getString("photo_description");
            if (isValid(rs.getString("phone"))) {
                currentFaculty.alternatePhone = rs.getString("phone");
            }
            currentFaculty.alternatePhonePreferred = rs.getInt("phone_preferred") == 1;
           
            // This is the preferred first name
            if (isValid(rs.getString("first_name")) && rs.getInt("first_name_preferred") == 1) {
                currentFaculty.firstName = rs.getString("first_name");
            }
            
            // This is the preferred middle name
            // Need to allow empty middle name
            if (rs.getInt("middle_name_preferred") == 1 ) {
                currentFaculty.middleName = rs.getString("middle_name");
            }
            
            // This is an additional email 
            if (isValid(rs.getString("email"))) {
                currentFaculty.alternateEmail = rs.getString("email");
            }
            currentFaculty.alternateEmailPreferred = rs.getInt("first_name_preferred") == 1;
            
            currentFaculty.titles = rs.getString("titles");
            currentFaculty.useFormEntryForPublications = (rs.getInt("publications_use_form_entry") == 1)? true : false;
            if (!(currentFaculty.useFormEntryForPublications)) {
                currentFaculty.publicationsText = rs.getString("publications_freeform");
            }
        }
        rs.close();
        stmt.close();

        // get module status If a sub-page module is inactive, make parent page inactive
        stmt = conn.prepareStatement(Queries.GetModuleActiveStatus);
        stmt.setInt(1, currentFaculty.facultyID);
        rs = stmt.executeQuery();
        while(rs.next()) {
            currentFaculty.active = (rs.getInt("active") == 1)?true:false;
            if (rs.getInt("bio_active") == 0) {
                currentFaculty.bioActive = false;
                currentFaculty.active = false;
            } else {
                currentFaculty.bioActive = true;
            }
            if (rs.getInt("courses_active") == 0) {
                currentFaculty.coursesActive = false;
                currentFaculty.active = false;

            } else {
                currentFaculty.coursesActive = true;
            }
            if (rs.getInt("education_active") == 0) {
                currentFaculty.educationActive = false;
                currentFaculty.active = false;

            } else {
                currentFaculty.educationActive = true;
            }
            if (rs.getInt("certificates_active") == 0) {
                currentFaculty.licensesCertificatesActive = false;
                currentFaculty.active = false;

            } else {
                currentFaculty.licensesCertificatesActive = true;
            }
            if (rs.getInt("links_active") == 0) {
                currentFaculty.linksActive = false;
                currentFaculty.active = false;

            } else {
                currentFaculty.linksActive = true;
            }
            if (rs.getInt("professional_services_active") == 0) {
                currentFaculty.professionalActivityActive = false;
            } else {
                currentFaculty.professionalActivityActive = true;
            }
            if (rs.getInt("publications_active") == 0) {
                currentFaculty.publicationsActive = false;
            } else {
                currentFaculty.publicationsActive = true;
            }
            if (rs.getInt("research_active") == 0) {
                currentFaculty.researchActive = false;
            } else {
                currentFaculty.researchActive = true;
            }
            /*if (rs.getInt("expert_active") == 0) {
                currentFaculty.expertActive = false;
            } else {
                currentFaculty.expertActive = true;
            }*/
        }
        rs.close();
        stmt.close();

        // get form-entered publications
        if (currentFaculty.useFormEntryForPublications) {
            List<Publication> publications = new ArrayList<Publication>();
            stmt = conn.prepareStatement(Queries.GetPublications);
            stmt.setInt(1, currentFaculty.facultyID);
            rs = stmt.executeQuery();
            while(rs.next()) {
                Publication p = new Publication();
                p.faculty = currentFaculty;
                p.title = rs.getString("title");
                p.name = rs.getString("name");
                p.publisher = rs.getString("publisher");
                p.month = rs.getInt("month");
                p.year = rs.getInt("year");
                p.volume = rs.getString("volume");
                p.issue = rs.getString("issue");
                p.page = rs.getString("page");
                p.abstr = rs.getString("abstract");
                p.url = rs.getString("url");
                p.authors = rs.getString("authors");
                p.publisherLocation = rs.getString("publisher_location");
                p.position = rs.getInt("position");
                p.publicationType = rs.getString("publication_type");
                publications.add(p);
            }
            currentFaculty.publications = publications;
            rs.close();
            stmt.close();
        }

        // Get Education
        currentFaculty.education = new Education(currentFaculty.towerID, currentFaculty.facultyID, conn);

        // Get licenses and Certificates
        List<License> licenses = new ArrayList<License>();
        stmt = conn.prepareStatement(Queries.LicenseQuery);
        stmt.setInt(1, currentFaculty.facultyID);
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
        currentFaculty.licenses = licenses;

        // Clean up license state and country
        for (License l : currentFaculty.licenses) {
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
            while (rs.next()) {
                l.state = rs.getString("descr");
            }
            rs.close();
            stmt.close();
        }

        // Get professional activities
        stmt = conn.prepareStatement(Queries.GetProfessionalActivities);
        // "SELECT * FROM sjsu_people_professional_activities WHERE faculty_id=? ORDER BY position";
        stmt.setInt(1, currentFaculty.facultyID);
        rs = stmt.executeQuery();
        List<ProfessionalActivity> activities = new ArrayList<ProfessionalActivity>();
        ProfessionalActivity activity = null;
        while (rs.next()) {
            activity = new ProfessionalActivity();
            activity.title = rs.getString("title");
            activity.startYear = rs.getInt("start_year");
            activity.endYear = rs.getInt("end_year");
            activity.description = rs.getString("description");
            activity.positionTitle = rs.getString("position_title");
            activities.add(activity);
        }
        currentFaculty.professionalActivities = activities;

        // Get job title(s)
        stmt = conn.prepareStatement(Queries.JobsQuery);
        stmt.setInt(1, currentFaculty.towerID);
        rs = stmt.executeQuery();
        List<Position> positions = new ArrayList<Position>();
        Position position = null;
        while (rs.next()) {
            position = new Position(rs.getString("position_descr"));
            position.department = rs.getString("deptid_descr");
            positions.add(position);
        }
        rs.close();
        stmt.close();
        currentFaculty.positions = positions;

        // Get home page links
        List<Link> homeLinks = new ArrayList<Link>();
        stmt = conn.prepareStatement(Queries.HomeLinksQuery);
        stmt.setInt(1, currentFaculty.facultyID);
        rs = stmt.executeQuery();
        Link homeLink = null;
        while(rs.next()) {
            homeLink = new Link(rs.getString("label"), rs.getString("url"));
            homeLinks.add(homeLink);
        }
        currentFaculty.links = homeLinks;
        rs.close();
        stmt.close();

        // Get Custom Pages
        List<CustomPage> customPages = new ArrayList<CustomPage>();
        stmt = conn.prepareStatement(Queries.GetCustomPages);
        // SELECT * FROM sjsu_people_pages WHERE faculty_id=?";
        stmt.setInt(1, currentFaculty.facultyID);
        rs = stmt.executeQuery();
        while(rs.next()) {
            CustomPage p = new CustomPage(rs.getInt("id"));
            p.name = rs.getString("name");
            p.title = rs.getString("title");
            p.content = rs.getString("content");
            //p.url = baseURL + currentFaculty.handle + "/" + p.name;
            p.url = currentFaculty.handle() + "/" + p.name;
            p.active = rs.getInt("status") == 1?true:false;
            customPages.add(p);
        }
        rs.close();
        stmt.close();
        currentFaculty.customPages = customPages;

        // get custome page links
        for (CustomPage p : currentFaculty.customPages) {
            List<Link> links = new ArrayList<Link>();
            stmt = conn.prepareStatement(Queries.GetCustomPageLinks);
            stmt.setInt(1, p.id);
            rs = stmt.executeQuery();
            while(rs.next()) {
                Link l = new Link(rs.getString("label"), rs.getString("url"));
                links.add(l);
            }
            p.links = links;
        }

        // get custom page documents
        for (CustomPage p : currentFaculty.customPages) {
            List<Doc> docs = new ArrayList<Doc>();
            stmt = conn.prepareStatement(Queries.GetCustomPageDocs);
            stmt.setInt(1, p.id);
            rs = stmt.executeQuery();
            while(rs.next()) {
                Doc d = new Doc(rs.getString("label"), "/people" + p.url 
                        + "/" + rs.getString("path").substring(rs.getString("path").lastIndexOf('/') + 1));
                docs.add(d);
                String sourceURL = liveSiteBaseDir + d.url;
                // Save document without illegal characters
                String destURL = outputDirectory + d.legalURL();
                try {
                       saveDocumentFromURL(sourceURL, destURL);
                    } catch(java.io.IOException e) {
                        e.printStackTrace();
                    }
            }
            p.documents = docs;
        }
        // Get Research
        List<Research> research = new ArrayList<Research>();
        Research researchItem = null;
        stmt = conn.prepareStatement(Queries.GetResearch);
        stmt.setInt(1, currentFaculty.facultyID);
        rs = stmt.executeQuery();
        while(rs.next()) {
            researchItem = new Research();
            researchItem.faculty = currentFaculty;
            researchItem.title = rs.getString("title");
            researchItem.sponsor = rs.getString("sponsor");
            researchItem.startYear = rs.getInt("start_year");
            researchItem.summary = rs.getString("summary");
            researchItem.endYear = rs.getInt("end_year");
            researchItem.organization = rs.getString("organization");
            researchItem.grant = rs.getString("grant");
            research.add(researchItem);
        }
        currentFaculty.research = research;

        // Get Courses
        List<Course> courses = new ArrayList<Course>();
        Course course = null;
        stmt = conn.prepareStatement(Queries.CoursesQuery);
        // SELECT * FROM sjsu_people_course_entry WHERE faculty_id=? ORDER BY position;
        stmt.setInt(1, currentFaculty.facultyID);
        rs = stmt.executeQuery();
        while(rs.next()) {
            course = new Course(rs.getString("title"));
            course.faculty = currentFaculty;
            course.name = rs.getString("name");
            course.location = rs.getString("location");
            course.dayAndTime = rs.getString("time");
            course.id = rs.getInt("id");
            course.active = rs.getInt("status") == 1?true:false;
            course.description = rs.getString("description");
            course.supplementalUrl = rs.getString("url");
            course.photoSetting = rs.getInt("photo_setting");
            course.photoDescription = rs.getString("photo_description");
            courses.add(course);
        }
        rs.close();
        stmt.close();
        currentFaculty.courses = courses;

        // process courses
        // get sections
        Section section = null;
        for (Course currentCourse : currentFaculty.courses) {
            List<Section> sections = new ArrayList<Section>();
            stmt = conn.prepareStatement(Queries.CourseSectionsQuery);
            // SELECT * FROM sjsu_people_course_section WHERE course_id=? ORDER BY position"
            stmt.setInt(1, currentCourse.id);
            rs = stmt.executeQuery();
            while(rs.next()) {
                section = new Section(rs.getInt("id"));
                section.name = rs.getString("name");
                section.description = rs.getString("description");
                section.position = rs.getInt("position");
                section.course_id = currentCourse.id;
                // If any section is inactive, make the course inactive
                if (rs.getInt("status") == 0) {
                    section.active = false;
                    currentCourse.active = false;
                    System.out.println("Section " + section.name + " is inactive, deactivating course " + currentCourse.title);
                } else {
                    section.active = true;
                }
                sections.add(section);
            }
            currentCourse.sections = sections;
            rs.close();
            stmt.close();

            // Get course photo
            String extension;
            if (currentCourse.photoSetting == 2) {
                extension = saveImage(currentCourse.path(), currentCourse.name);
                currentCourse.photoExtension = extension;
            }
			String destPath = "";
            // get docs for section
            for (Section currentSection : currentCourse.sections) {
                // System.out.println("\n\nCopying section files from /fac directory to output directory");
                List<Doc> documents = new ArrayList<Doc>();
                stmt = conn.prepareStatement(Queries.SectionDocsQuery);
                // SELECT spd.path, spd.label  FROM sjsu_people_course_section_docs spcsd, sjsu_people_documents spd 
                // WHERE spd.id = spcsd.document_id AND spcsd.course_section_id=? ORDER BY spcsd.position
                stmt.setInt(1, currentSection.id);
                rs = stmt.executeQuery();
                String cmd = "";
                String filename = "";
                while(rs.next()) {
                    // Get the name of the file from the path
                    filename = rs.getString("path").substring(rs.getString("path").lastIndexOf('/') + 1);

                    // Create a new Doc object
                    Doc currentDoc = new Doc(rs.getString("label"), currentCourse.path() + currentSection.url() + "/"  + filename);
                    currentDoc.name = filename;

                    // /fac/<handle>/course/<courseID>/section/<sectionID>/
                    currentDoc.localPath = localDocRoot + currentFaculty.handle() + "/course/" + currentCourse.id 
                        + "/section/" + currentSection.id + "/" + filename;
                    
                    // copy to output directory with a name that OU can handle
                    destPath = outputDirectory + currentCourse.path() + currentSection.url() + "/" + currentDoc.legalName();

                    // remove spaces?
                    // destPath = destPath.replaceAll(" ", "");
                    
                    // command to copy file
                    String[] cmdArray = new String[]{"cp", currentDoc.localPath, destPath};
                    
                    // Copy the file
                    try {
                       new File(outputDirectory + currentCourse.path() + currentSection.url()).mkdirs();
                       Process cmdProc = Runtime.getRuntime().exec(cmdArray);
                       BufferedReader stdoutReader = new BufferedReader(
                           new InputStreamReader(cmdProc.getInputStream()));
                       String cmdOutput;
                       while ((cmdOutput = stdoutReader.readLine()) != null) {
                            // process procs standard output here
                            System.out.println(cmdOutput);
                       }

                       BufferedReader stderrReader = new BufferedReader(
                           new InputStreamReader(cmdProc.getErrorStream()));
                       String cmdError;
                       while ((cmdError = stderrReader.readLine()) != null) {
                            System.out.println("Error: " + cmdError + "\n");
                            // process procs standard error here
                       }

                    } catch (IOException e) {
                      e.printStackTrace();
                    }

                  documents.add(currentDoc);
                    
                }
                currentSection.docs = documents;
                rs.close();
                stmt.close();

                List<Link> sectionLinks = new ArrayList<Link>();
                stmt = conn.prepareStatement(Queries.SectionLinksQuery);
                stmt.setInt(1, currentSection.id);
                rs = stmt.executeQuery();
                while(rs.next()) {
                    Link l = new Link(rs.getString("label"), rs.getString("url"));
                    sectionLinks.add(l);
                }
                currentSection.links = sectionLinks;
                rs.close();
                stmt.close();
            } 
        }

        // Output pics and docs folders
        String picsDir = outputDirectory + "/people" + currentFaculty.handle() + "/pics";
        File pics = new File(picsDir);
        pics.mkdirs();

        String docsDir = outputDirectory + "/people" + currentFaculty.handle() + "/docs";
        File docs = new File(docsDir);
        docs.mkdirs();
       
        System.out.println("Finished " + currentFaculty.fullName() + "\n");
    }

    /**
     * Find out if a string contains valid content
     * @param s Input string
     * @return True if it should be printed
     */
    static boolean isValid(String s) {
        if (s == null || s.equals("null") || s.trim().equals("")) {
            return false;
        } else if (s.length() < 1) {
            return false;
        } 
        return true;
    }

    /**
    * Download an image
    */
    static String saveImage(String path, String name) {
    	boolean success = false;
        String extension = "";
    	int count = -1;
    	try {
    		while (!success && ++count < (StringConstants.imageExtensions.length)) {
                extension = StringConstants.imageExtensions[count];
     			success = saveDocumentFromURL(liveSiteBaseDir + path + "/" + name + extension, 
     				outputDirectory + path + "/" + name + extension);
                
     			// System.out.println(StringConstants.imageExtensions[count]);
			}
            
        } catch(java.io.IOException e) {
            e.printStackTrace();
        }
        return extension;
    }

    /**
     * Read a file from the Web, save as file
     * @param documentUrl
     * @param destinationFile
     * @throws IOException
     */
    static boolean saveDocumentFromURL(String currentURL, String destinationFile) throws IOException {
        if (!Migrate.suppressFileOutput) {
            try {
                // Need to replace spaces with %20
                currentURL = currentURL.replaceAll(" ","%20");
                URL url = new URL(currentURL);

                URLConnection connection = url.openConnection();
                connection.connect();
               // Cast to a HttpURLConnection
                if (connection instanceof HttpURLConnection) {
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    if (httpConnection.getResponseCode() != 200) {
                        System.out.println("*** File not found: " + currentURL);
                        return false;
                    } else {
                        // System.out.println("Downloading: " + currentURL);
                        InputStream is = url.openStream();
                        String dir = destinationFile.substring(0, destinationFile.lastIndexOf('/'));
                        // Create local directories to write to
                        new File(dir).mkdirs();
                        OutputStream os = new FileOutputStream(destinationFile);
                        byte[] b = new byte[2048];
                        int length;
                        while ((length = is.read(b)) != -1) {
                            os.write(b, 0, length);
                        }
                        is.close();
                        os.close();
                        return true;
                    }
                }
            } catch (java.io.FileNotFoundException fnfe){
                System.out.println("Failed to download " + currentURL);
                return false;
            }
        } else {
        	return true;
        }
        return false;
    }
}



