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
		String result = "<li><strong>" + description + "</strong><br />";
		if (Migrate.isValid(positionTitle)) {
			result += positionTitle + ", ";
		}
		result += startYear + "-" + endYear;
		result += "<p>Description: " + description + "</p></li>"; 
		return result;
	}
}
