package faculty;

/**
 * 
 * @author Scot Close
 *
 */

/*
 * <li>
	<strong>The Planetary Society</strong>
	<br />
	Member, 1981-present
	
	<p><span class="text_blue">Description:</span>After a decade of success with Cassini at Saturn, the twin rovers Spirit and Opportunity, and the take-your-breath-away excitement of the skycrane landing of Curiosity on Mars, NASA's planetary exploration program was rewarded with budget cuts that seriously impair NASA's ability to explore the Solar System.</p>
	</li>
 */

public class ProfessionalActivity {
	String title;
	int startYear;
	int endYear;
	String description;
	String positionTitle;
	
	String toHTML(){
		String result = "<li><strong>" + description + "</strong><br />";
		if (Migrate.isValid(positionTitle)) {
			result += positionTitle + ", ";
		}
		result += startYear + "-" + endYear;
		result += "<p>Description: " + description + "</p></li>"; 
		return result;
	}
}
