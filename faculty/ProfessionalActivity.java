package faculty;

/**
 * 
 * @author Scot Close
 *
 */

public class ProfessionalActivity {
	String title;
	int startYear;
	int endYear;
	String description;
	String positionTitle;
	
/**
 * Return an HTML string
 * @return
 */
	String toHTML() {
		String result = "<li>";
		if (Migrate.isValid(title)) {
			result += "<strong>" + title + "</strong><br />";
		}	
		if (Migrate.isValid(positionTitle)) {
			result += positionTitle + ", ";
		}
		result += startYear + "-";
		if (endYear > 0) { 
			result += endYear;
		}
		if (Migrate.isValid(description)) {
			result += "<p>Description: " + description + "</p>";
		}	
		result += "</li>"; 
		return result;
	}
}
