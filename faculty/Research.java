package faculty;

class Research {
	int faculty_id;
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
	String toHTML() {
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
}