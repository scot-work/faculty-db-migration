package faculty;

class Research {
	int faculty_id;
	Faculty faculty;
	String title;
	String sponsor;
	int startYear;
	int endYear;
	String organization;
	String summary;
	int position;
	String grant;
	
	/**
	 * Output one Research item
	 * @param writer
	 */
	String getContentAsHtml() {
		String result = "<li><strong>";
		result += title;
		result += "</strong><br />";
		if (Migrate.isValid(sponsor) && Migrate.isValid(grant)) {
			result += sponsor + ", " + grant + "<br />";
		} else if (Migrate.isValid(sponsor)){
			result += sponsor + "<br />";
		} else if (Migrate.isValid(grant)){
			result += grant + "<br />";
		}
		if (Migrate.isValid(sponsor)){
			result += sponsor + ", ";
		}
		result += startYear + "-" + endYear;
		result += "<p>Description: " + summary + "</p></li>";
		return result;
		
	}
	/**
	 * Output as a pcf page
	 */
	void writePcf(){
		String content = getContentAsHtml();
		String path = faculty.handle + "/research/";
		XmlHelper.toXml(faculty, content, path);
	}
}