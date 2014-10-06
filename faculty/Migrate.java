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



/**
 * 
 * @author Scot Close
 *
 */
public class Migrate {

    public static String baseURL;
    public static String outputDirectory;
    public static String liveSiteBaseDir;
    static Boolean suppressFileOutput = false;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        Properties prop = new Properties();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        // Choose live.properties or dev.properties  
        InputStream stream = loader.getResourceAsStream("dev.properties");
        try {
            prop.load(stream);
        } catch (IOException e ) {
            e.printStackTrace();
        }
        Migrate.outputDirectory = prop.getProperty("outputDirectory");
        Migrate.liveSiteBaseDir = prop.getProperty("liveSiteBaseDir");
        Migrate.baseURL = prop.getProperty("baseURL");
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
            } else if  (args[0].equals("pcf")){
                outputEmptyPcfs();
            } 
        }
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
        } catch(ParserConfigurationException pce){
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
        PreparedStatement stmt = conn.prepareStatement(Queries.PublishedQuery);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            // Create a new Faculty object and output pages
            Faculty faculty = new Faculty(rs.getInt("faculty_id"));
            faculty.lastName = rs.getString("last_name");
            faculty.firstName = rs.getString("first_name");
            faculty.handle = rs.getString("handle");
            processFacultySite(conn, faculty);
            if (faculty.isActive) {
                faculty.output();
            }
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
        stmt.setInt(1, currentFaculty.facultyID);
        ResultSet rs = stmt.executeQuery();
        int id = 0;
        while (rs.next()) {
            id = rs.getInt("towerid");
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
        currentFaculty.isActive = false;
        while(rs.next()) {
            currentFaculty.isActive = true;
            currentFaculty.sjsuEmail = rs.getString("email_addr");
            currentFaculty.firstName = rs.getString("first_name");
            currentFaculty.middleName = rs.getString("middle_name");
            currentFaculty.lastName = rs.getString("last_name");
            currentFaculty.phone = rs.getString("phone");
        }
        rs.close();
        stmt.close();
        
        // Self-entered information
        stmt = conn.prepareStatement(Queries.DetailsQuery);
        stmt.setInt(1, currentFaculty.facultyID);
        rs = stmt.executeQuery();
        while(rs.next()) {
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
            if (isValid(rs.getString("middle_name")) && rs.getInt("middle_name_preferred") == 1) {
                currentFaculty.middleName = rs.getString("middle_name");
            }
            
            // This is an additional email 
            if (isValid(rs.getString("email"))) {
                currentFaculty.alternateEmail = rs.getString("email");
            }
            currentFaculty.alternateEmailPreferred = rs.getInt("first_name_preferred") == 1;
            
            currentFaculty.titles = rs.getString("titles");
            currentFaculty.handle = rs.getString("published_handle");
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
                currentFaculty.professionalServicesActive = false;

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
            while(rs.next()) {
                l.state = rs.getString("descr");
            }
            rs.close();
            stmt.close();
        }

        // Get job title(s)
        stmt = conn.prepareStatement(Queries.JobsQuery);
        stmt.setInt(1, currentFaculty.towerID);
        rs = stmt.executeQuery();
        List<Position> positions = new ArrayList<Position>();
        Position position = null;
        while(rs.next()) {
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
        stmt.setInt(1, currentFaculty.facultyID);
        rs = stmt.executeQuery();
        while(rs.next()) {
            CustomPage p = new CustomPage(rs.getInt("id"));
            p.name = rs.getString("name");
            p.title = rs.getString("title");
            p.content = rs.getString("content");
            //p.url = baseURL + currentFaculty.handle + "/" + p.name;
            p.url = currentFaculty.handle + "/" + p.name;
            p.active = rs.getInt("status") == 1?true:false;
            customPages.add(p);
        }
        rs.close();
        stmt.close();
        currentFaculty.customPages = customPages;

        // get links
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

        // get documents
        for (CustomPage p : currentFaculty.customPages) {
            List<Doc> docs = new ArrayList<Doc>();
            stmt = conn.prepareStatement(Queries.GetCustomPageDocs);
            stmt.setInt(1, p.id);
            rs = stmt.executeQuery();
            while(rs.next()) {
                Doc d = new Doc(rs.getString("label"), p.url 
                        + "/" + rs.getString("path").substring(rs.getString("path").lastIndexOf('/') + 1));
                docs.add(d);
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
        for (Course c : currentFaculty.courses) {
            List<Section> sections = new ArrayList<Section>();
            stmt = conn.prepareStatement(Queries.CourseSectionsQuery);
            // SELECT * FROM sjsu_people_course_section WHERE course_id=? ORDER BY position"
            stmt.setInt(1, c.id);
            rs = stmt.executeQuery();
            while(rs.next()) {
                section = new Section(rs.getInt("id"));
                section.name = rs.getString("name");
                section.description = rs.getString("description");
                // If any section is inactive, make the course inactive
                if (rs.getInt("status") == 0) {
                    section.active = false;
                    c.active = false;
                    System.out.println("Section " + section.name + " is inactive, deactivating course " + c.title);
                } else {
                    section.active = true;
                }
                //section.url = c.url() + "/s" + rs.getString("position");
                section.url = c.url() + "/s1";
                sections.add(section);
            }
            c.sections = sections;
            rs.close();
            stmt.close();

            // get docs for section
            for (Section s : c.sections) {
                List<Doc> documents = new ArrayList<Doc>();
                stmt = conn.prepareStatement(Queries.SectionDocsQuery);
                stmt.setInt(1, s.id);
                rs = stmt.executeQuery();
                while(rs.next()) {
                    String label = rs.getString("label");
                    String filename = "";
                    try {
                        filename = URLEncoder.encode(rs.getString("path").substring(rs.getString("path").lastIndexOf('/') + 1), "UTF-8");
                    } catch (java.io.UnsupportedEncodingException e){
                        e.printStackTrace();
                    }   
                    Doc d = new Doc(rs.getString("label"), s.url + "/"  + filename);
                    //Doc d = new Doc(label, s.url + "/" 
                    //        + rs.getString("path").URLEncoder.encode(substring(rs.getString("path").lastIndexOf('/') + 1), "UTF-8"));
                    documents.add(d);
                    String sourceURL = liveSiteBaseDir + d.url;
                    String destURL = outputDirectory + d.url;
                    try {
                       saveDocument(sourceURL, destURL);
                    } catch(java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
                s.docs = documents;
                rs.close();
                stmt.close();

                List<Link> sectionLinks = new ArrayList<Link>();
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
        }

        System.out.println("Finished " + currentFaculty.fullName());
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
     * Read an image from the Web, save as file
     * @param documentUrl
     * @param destinationFile
     * @throws IOException
     */
    static void saveDocument(String documentUrl, String destinationFile) throws IOException {
        if (!Migrate.suppressFileOutput){
            try {
                URL url = new URL(documentUrl);

                URLConnection connection = url.openConnection();
                connection.connect();
               // Cast to a HttpURLConnection
                if ( connection instanceof HttpURLConnection) {
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    if (httpConnection.getResponseCode() == 404) {
                        System.out.println("Failed to download " + documentUrl);
                    }
                }

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
            } catch (java.io.FileNotFoundException fnfe){
                fnfe.printStackTrace();
            }
        }
    }
}



