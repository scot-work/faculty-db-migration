package faculty;

import java.util.*;
import java.sql.*;

/**
 * Degree information is in these tables:
 * sjsu_people_education_last_degree_on_file_master
 * sjsu_people_education_terminal_master
 * sjsu_people_education
 * 
 * sjsu_people_details.use_terminal_degree (0 or 1)
 * 
 * Get a user-added degree:
 * SELECT sdm.descr, smm.descr, ssm.descr, scm.descr, spe.year 
 * FROM sjsu_countries_master scm, sjsu_schools_master ssm, sjsu_people_education spe, sjsu_degrees_master sdm, sjsu_majors_master smm 
 * WHERE spe.faculty_id=? AND spe.degree_code = sdm.degree AND spe.major_code = smm.major_code AND spe.school_code = ssm.school_code AND spe.country_code = scm.country;
 *
 * Get official degree
 * SELECT * FROM sjsu_people_education_last_degree_on_file_master WHERE emplid=?
 * 
 */

class Education {
	/* public static String OfficialDegreeQuery = "SELECT * FROM sjsu_people_education_last_degree_on_file_master WHERE emplid=?";
  public static String AdditionalDegreeQuery = "SELECT * FROM sjsu_people_education WHERE faculty_id=?";
  public static String GetMajorFromCode = "SELECT descr FROM sjsu_majors_master WHERE major_code=?";
  public static String GetSchoolFromCode = "SELECT descr FROM sjsu_schools_master WHERE school_code=?";
  public static String GetDegreeFromCode = "SELECT descr FROM sjsu_degrees_master WHERE degree=?";
  public static String GetCountryFromCode = "SELECT descr FROM sjsu_countries_master WHERE country=?";
  public static String GetStateFromCode = "SELECT descr FROM sjsu_states_master WHERE country=? AND state=?"; */

	List<Degree> degrees;

/**
 * Create a new Education object
 * @param towerID
 * @param facultyID
 * @param conn
 * @throws java.sql.SQLException
 */
	public Education(int towerID, int facultyID, Connection conn) throws java.sql.SQLException {
		// System.out.println("Getting education for " + towerID);
		// get list of degrees SELECT * FROM sjsu_people_education_last_degree_on_file_master WHERE emplid=?
		String degree = "";
		String major = "";
		String schoolName = "";
		//String country = "";
		String year = "";
		//String degreeCode = "";
		//String degreeName = "";
		String schoolCode = "";
		String majorCode = "";
		//String majorName = "";
		//String countryCode = "";
		//String stateCode = "";
		//String stateName = "";
		degrees = new ArrayList<Degree>();

		// Get officially listed degree (should return one row per faculty)
		PreparedStatement stmt = conn.prepareStatement(Queries.GetOfficialDegree);
		// SELECT * FROM sjsu_people_education_last_degree_on_file_master WHERE emplid=?
		stmt.setInt(1, towerID);
		ResultSet rs = stmt.executeQuery();

		while(rs.next()) {
			degree = rs.getString("degree");
			majorCode = rs.getString("major_code");
			major = rs.getString("major");
			schoolCode = rs.getString("school_code");
			schoolName = rs.getString("school");
			year = rs.getString("year");
		}
		rs.close();
		stmt.close();

		Degree officialDegree = new Degree(degree);
		if (year != null) officialDegree.year = year;
		// major
		if (majorCode == null && major != null) {
			officialDegree.major = major; 
		} else if (majorCode != null) {
			stmt = conn.prepareStatement(Queries.GetMajorFromCode);
			stmt.setString(1, majorCode);
			rs = stmt.executeQuery();
			while(rs.next()) {
				major = rs.getString("descr");
			}
			officialDegree.major = major;
		}
		// school
		if (schoolCode == null && schoolName != null){
			officialDegree.school = schoolName;
		} else if (schoolCode != null) {
			stmt = conn.prepareStatement(Queries.GetSchoolFromCode);
			stmt.setString(1, schoolCode);
			rs = stmt.executeQuery();
			while(rs.next()) {
				schoolName = rs.getString("descr");
			}
			rs.close();
			stmt.close();
			officialDegree.school = schoolName;
		}
		degrees.add(officialDegree);

		// Look for additional degrees
		Degree additionalDegree = null;
		stmt = conn.prepareStatement(Queries.GetAdditionalDegree);
		stmt.setInt(1, facultyID);
		rs = stmt.executeQuery();
		while(rs.next()) {
			additionalDegree = new Degree();
			additionalDegree.degreeCode = rs.getString("degree_code");
			additionalDegree.majorCode = rs.getString("major_code");
			additionalDegree.schoolCode = rs.getString("school_code");
			additionalDegree.schoolName = rs.getString("school_name");
			additionalDegree.year = rs.getString("year");
			additionalDegree.degreeName = rs.getString("degree_name");
			additionalDegree.majorName = rs.getString("major_name");
			additionalDegree.countryCode = rs.getString("country_code");
			additionalDegree.stateCode = rs.getString("state_code");
			additionalDegree.stateName = rs.getString("state_name");
			degrees.add(additionalDegree);
		}
		rs.close();
		stmt.close();

		// clean up additional degrees
		if (degrees.size() > 1) {
			for (Degree d : degrees) {
				// degree
				if (d.degreeCode == null && d.degreeName != null) {
					d.degree = d.degreeName;
				} else {
					if (d.degreeCode != null) {
						stmt = conn.prepareStatement(Queries.GetDegreeFromCode);
						stmt.setString(1, d.degreeCode);
						rs = stmt.executeQuery();
						while(rs.next()) {
							d.degree = rs.getString("descr");
						}
						rs.close();
						stmt.close();
					}
				}
				// major
				if (d.majorCode == null && d.majorName != null) {
					d.major = d.majorName;
				} else if (d.majorCode != null) {
					stmt = conn.prepareStatement(Queries.GetMajorFromCode);
					stmt.setString(1, d.majorCode);
					rs = stmt.executeQuery();
					while(rs.next()) {
						d.major = rs.getString("descr");
					}
					rs.close();
					stmt.close();
				}

				// school
				if (d.schoolCode == null && d.schoolName != null) {
					d.school = d.schoolName;
				} else if (d.schoolCode != null) {
					stmt = conn.prepareStatement(Queries.GetSchoolFromCode);
					stmt.setString(1, d.schoolCode);
					rs = stmt.executeQuery();
					while(rs.next()) {
						d.school = rs.getString("descr");
					}
					rs.close();
					stmt.close();
				}
				// state
				if (d.stateCode == null && d.stateName != null) {
					d.state = d.stateName;
				} else if (d.stateCode != null) {
					stmt = conn.prepareStatement(Queries.GetStateFromCode);
					stmt.setString(1, d.countryCode);
					stmt.setString(2, d.stateCode);
					rs = stmt.executeQuery();
					while(rs.next()) {
						d.state = rs.getString("descr");
					}
					rs.close();
					stmt.close();
				}
				// country
				stmt = conn.prepareStatement(Queries.GetCountryFromCode);
				stmt.setString(1, d.countryCode);
				rs = stmt.executeQuery();
				while(rs.next()) {
					d.country = rs.getString("descr");
				}
				rs.close();
				stmt.close();
			}
		} 
	}
}
