package faculty;

class Queries {
	// Get official degree for one faculty
	public static String OfficialDegreeQuery = "SELECT * " +
			"FROM sjsu_people_education_last_degree_on_file_master " +
			"WHERE emplid=?";
	
	// Get all self-entered degrees for one faculty
	public static String AdditionalDegreeQuery = "SELECT * FROM sjsu_people_education " +
			"WHERE faculty_id=?";
	
	// Get display name for a major
	public static String GetMajorFromCode = "SELECT descr FROM sjsu_majors_master " +
			"WHERE major_code=?";
	
	// Get display name for a school
	public static String GetSchoolFromCode = "SELECT descr FROM sjsu_schools_master " +
			"WHERE school_code=?";
	
	// Get display name for a degree
	public static String GetDegreeFromCode = "SELECT descr FROM sjsu_degrees_master " +
			"WHERE degree=?";
	
	// Get display name for a country
	public static String GetCountryFromCode = "SELECT descr FROM sjsu_countries_master " +
			"WHERE country=?";
	
	// Get display name for a state
	public static String GetStateFromCode = "SELECT descr FROM sjsu_states_master " +
			"WHERE country=? AND state=?";
	
	// Get list of all published faculty sites
	public static String PublishedQuery = "SELECT * FROM sjsu_people_published";
	
	// Get SJSU ID from internal ID
	public static String TowerIDQuery = "SELECT towerid FROM sjsu_people_users " +
			"WHERE faculty_id=?";
	
	// Get self-entered information about a faculty
	public static String DetailsQuery = "SELECT * FROM sjsu_people_details WHERE id=?";
	
	// Get official position(s) description for a faculty
	public static String JobsQuery = "SELECT position_descr, deptid_descr " +
			"FROM sjsu_people_job_details_master WHERE towerid=?";
	
	// Get official contact information about a faculty
	public static String OfficialInfoQuery = "SELECT * FROM sjsu_people_details_master " +
			"WHERE towerid=?";
	
	// Get a list of courses for a faculty
	public static String CoursesQuery = "SELECT * FROM sjsu_people_course_entry " +
			"WHERE faculty_id=? ORDER BY position;";
	
	// Get list of page sections for one course
	public static String CourseSectionsQuery = "SELECT * FROM sjsu_people_course_section " +
			"WHERE course_id=? ORDER BY position";
	
	// Get a list of documents from a course section
	public static String SectionDocsQuery = "SELECT spd.path, spd.label " +
			"FROM sjsu_people_course_section_docs spcsd, sjsu_people_documents spd " +
			"WHERE spd.id = spcsd.document_id AND spcsd.course_section_id=? " +
			"ORDER BY spcsd.position";
	
	// Get a list of links from a course section
	public static String SectionLinksQuery = "SELECT * FROM sjsu_people_course_section_links " +
			"WHERE course_section_id=?";
	
	// Get details about a document
	public static String DocumentDetails = "SELECT * FROM sjsu_people_documents WHERE id=?";
	
	// Get details about a link
	public static String LinkDetails = "SELECT * FROM sjsu_people_links WHERE id=?";
	
	// Get license information
	public static String LicenseQuery = "SELECT * FROM sjsu_people_licenses_certificates splc "
			+ "LEFT JOIN sjsu_licenses_certificates_master slcm "
			+ "ON splc.license_code = slcm.accomplishment WHERE splc.faculty_id=?";
	
	// Get home page links
	public static String HomeLinksQuery = "SELECT url, label FROM sjsu_people_links "
			+ "WHERE faculty_id =?";
	
	// Get publications
	// sjsu_publication_author_master identifier, description
	// sjsu_publication_type_master
	public static String GetPublications = "SELECT * FROM sjsu_people_publications " +
			"WHERE faculty_id=? "
			+ "ORDER BY position";
	
	// Get research
	public static String GetResearch = "SELECT * FROM sjsu_people_research " +
			"WHERE faculty_id=? "
			+ "ORDER BY position";
	
	// Get module active/inactive status
	public static String GetModuleActiveStatus = "SELECT * FROM sjsu_people_website_settings "
			+ "WHERE faculty_id=?";
	
	// Get custom pages
	public static String GetCustomPages = "SELECT * FROM sjsu_people_pages "
			+ "WHERE faculty_id=?";
	
	// Get professional activity
	public static String GetProfessionalActivities = "SELECT * FROM sjsu_people_professional_activities "
				+ "WHERE faculty_id=? ORDER BY position";

	// Get links from custom page
	public static String GetCustomPageLinks = "SELECT label, url FROM sjsu_people_page_links "
			+ "WHERE page_id=? ORDER BY position";
	// Get documents from custom page
	public static String GetCustomPageDocs = "SELECT spd.label, spd.path "
			+ "FROM sjsu_people_page_docs sppd, sjsu_people_documents spd "
			+ "WHERE sppd.page_id=? AND sppd.document_id = spd.id ORDER BY sppd.position";
}
