package faculty;

import java.io.PrintWriter;

/*
<li>
	
<strong>Activity</strong><br />
Sponsor, Grant<br />
Organization, 1958-present<br />

<p>
<span class="text_blue">Description: </span>
This is a description of the research and scholarly activity.
</p>
</li>
 */

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
	 * Constructor
	 */
	Research(){

	}
	
	/**
	 * Output one Research item
	 * @param writer
	 */
	void toHTML(PrintWriter writer) {
		writer.print("<li><strong>");
		writer.print(title);
		writer.println("</strong><br />");
		if (Migrate.isValid(sponsor) && Migrate.isValid(grant)) {
			writer.println(sponsor + ", " + grant + "<br />");
		} else if (Migrate.isValid(sponsor)){
			writer.println(sponsor + "<br />");
		} else if (Migrate.isValid(grant)){
			writer.println(grant + "<br />");
		}
		if (Migrate.isValid(sponsor)){
			writer.print(sponsor + ", ");
		}
		writer.println(startYear + "-" + endYear);
		writer.println("<p>Description: " + summary + "</p></li>");
		
	}
}